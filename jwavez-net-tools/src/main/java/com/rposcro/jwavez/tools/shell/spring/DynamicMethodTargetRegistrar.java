package com.rposcro.jwavez.tools.shell.spring;

import com.rposcro.jwavez.tools.shell.JWaveZShellContext;
import com.rposcro.jwavez.tools.shell.commands.CommandGroup;
import lombok.Builder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.Command;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.MethodTargetRegistrar;
import org.springframework.shell.Utils;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This registrar implementation is just a simple copy&paste + enhancement to the original StandardMethodTargetRegistrar
 * Refer to https://github.com/spring-projects/spring-shell/blob/master/spring-shell-standard/src/main/java/org/springframework/shell/standard/StandardMethodTargetRegistrar.java
 */
public class DynamicMethodTargetRegistrar implements MethodTargetRegistrar {

    private static final String[] GROUPS_ALWAYS_ON = { "Built-In Commands", CommandGroup.GENERIC };

    private ApplicationContext applicationContext;
    private JWaveZShellContext shellContext;

  //  private Map<String, MethodTarget> commands;

    @Builder
    public DynamicMethodTargetRegistrar(ApplicationContext applicationContext, JWaveZShellContext shellContext) {
        this.applicationContext = applicationContext;
        this.shellContext = shellContext;
    }

    @Override
    public void register(ConfigurableCommandRegistry registry) {
        Map<String, Object> commandBeans = applicationContext.getBeansWithAnnotation(ShellComponent.class);
        for (Object bean : commandBeans.values()) {
            Class<?> clazz = bean.getClass();
            ReflectionUtils.doWithMethods(clazz, method -> {
                ShellMethod shellMapping = method.getAnnotation(ShellMethod.class);
                String[] keys = shellMapping.key();
                if (keys.length == 0) {
                    keys = new String[] { Utils.unCamelify(method.getName()) };
                }

                String group = getOrInferGroup(method);
                if (isGroupAllowedForCurrentScope(group)) {
                    for (String key : keys) {
                        Supplier<Availability> availabilityIndicator = findAvailabilityIndicator(keys, bean, method);
                        MethodTarget target = new MethodTarget(method, bean, new Command.Help(shellMapping.value(), group), availabilityIndicator);
                        registry.register(key, target);
//                    commands.put(key, target);
                    }
                }
            }, method -> method.getAnnotation(ShellMethod.class) != null);
        }
    }

    private boolean isGroupAllowedForCurrentScope(String group) {
        if (shellContext.getShellScope().name().equalsIgnoreCase(group)) {
            return true;
        }
        return Stream.of(GROUPS_ALWAYS_ON).anyMatch(groupOn -> groupOn.equals(group));
    }

    private String getOrInferGroup(Method method) {
        ShellMethod methodAnn = AnnotationUtils.getAnnotation(method, ShellMethod.class);
        if (!methodAnn.group().equals(ShellMethod.INHERITED)) {
            return methodAnn.group();
        }
        Class<?> clazz = method.getDeclaringClass();
        ShellCommandGroup classAnn = AnnotationUtils.getAnnotation(clazz, ShellCommandGroup.class);
        if (classAnn != null && !classAnn.value().equals(ShellCommandGroup.INHERIT_AND_INFER)) {
            return classAnn.value();
        }
        ShellCommandGroup packageAnn = AnnotationUtils.getAnnotation(clazz.getPackage(), ShellCommandGroup.class);
        if (packageAnn != null && !packageAnn.value().equals(ShellCommandGroup.INHERIT_AND_INFER)) {
            return packageAnn.value();
        }
        // Shameful copy/paste from https://stackoverflow.com/questions/7593969/regex-to-split-camelcase-or-titlecase-advanced
        return StringUtils.arrayToDelimitedString(clazz.getSimpleName().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), " ");
    }

    private Supplier<Availability> findAvailabilityIndicator(String[] commandKeys, Object bean, Method method) {
        ShellMethodAvailability explicit = method.getAnnotation(ShellMethodAvailability.class);
        final Method indicator;
        if (explicit != null) {
            Assert.isTrue(explicit.value().length == 1, "When set on a @" +
                    ShellMethod.class.getSimpleName() + " method, the value of the @"
                    + ShellMethodAvailability.class.getSimpleName() +
                    " should be a single element, the name of a method that returns "
                    + Availability.class.getSimpleName() +
                    ". Found " + Arrays.asList(explicit.value()) + " for " + method);
            indicator = ReflectionUtils.findMethod(bean.getClass(), explicit.value()[0]);
        } // Try "<method>Availability"
        else {
            Method implicit = ReflectionUtils.findMethod(bean.getClass(), method.getName() + "Availability");
            if (implicit != null) {
                indicator = implicit;
            } else {
                Map<Method, Collection<String>> candidates = new HashMap<>();
                ReflectionUtils.doWithMethods(bean.getClass(), candidate -> {
                    List<String> matchKeys = new ArrayList<>(Arrays.asList(candidate.getAnnotation(ShellMethodAvailability.class).value()));
                    if (matchKeys.contains("*")) {
                        Assert.isTrue(matchKeys.size() == 1, "When using '*' as a wildcard for " +
                                ShellMethodAvailability.class.getSimpleName() + ", this can be the only value. Found " +
                                matchKeys + " on method " + candidate);
                        candidates.put(candidate, matchKeys);
                    } else {
                        matchKeys.retainAll(Arrays.asList(commandKeys));
                        if (!matchKeys.isEmpty()) {
                            candidates.put(candidate, matchKeys);
                        }
                    }
                }, m -> m.getAnnotation(ShellMethodAvailability.class) != null && m.getAnnotation(ShellMethod.class) == null);

                // Make sure wildcard approach has less precedence than explicit name
                Set<Method> notUsingWildcard = candidates.entrySet().stream()
                        .filter(e -> !e.getValue().contains("*"))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

                Assert.isTrue(notUsingWildcard.size() <= 1,
                        "Found several @" + ShellMethodAvailability.class.getSimpleName() +
                                " annotated methods that could apply for " + method + ". Offending candidates are "
                                + notUsingWildcard);

                if (notUsingWildcard.size() == 1) {
                    indicator = notUsingWildcard.iterator().next();
                } // Wildcard was available
                else if (candidates.size() == 1) {
                    indicator = candidates.keySet().iterator().next();
                } else {
                    indicator = null;
                }
            }
        }

        if (indicator != null) {
            Assert.isTrue(indicator.getReturnType().equals(Availability.class),
                    "Method " + indicator + " should return " + Availability.class.getSimpleName());
            Assert.isTrue(indicator.getParameterCount() == 0, "Method " + indicator + " should be a no-arg method");
            ReflectionUtils.makeAccessible(indicator);
            return () -> (Availability) ReflectionUtils.invokeMethod(indicator, bean);
        }
        else {
            return null;
        }
    }
}
