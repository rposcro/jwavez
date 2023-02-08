package com.rposcro.jwavez.tools.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class BeanPropertiesFormatter {

    private final Function<Object, String> byteFormatter;
    private final Function<Object, String> shortFormatter;
    private final Function<Object, String> intFormatter;
    private final Function<Object, String> longFormatter;
    private final Function<Object, String> enumFormatter;
    private final Function<Object, String> objectFormatter;

    public BeanPropertiesFormatter() {
        this.byteFormatter = (value) -> String.format("%02X", value);
        this.shortFormatter = (value) -> String.format("%02X", value);
        this.intFormatter = (value) -> String.format("%04X", value);
        this.longFormatter = (value) -> String.format("%08X", value);
        this.enumFormatter = (value) -> ((Enum) value).name();
        this.objectFormatter = (value) -> value.toString();
    }

    public Map<String, String> collectBeanProperties(Object bean) throws Exception {
        HashMap<String, String> properties = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), bean.getClass().getSuperclass());
        Arrays.stream(beanInfo.getPropertyDescriptors()).forEach(descriptor -> {
            try {
                properties.put(descriptor.getDisplayName(), formatPropertyValue(descriptor, bean));
            } catch (Exception e) {
                log.info("Skipped property " + descriptor.getDisplayName() + " skipped due to an exception " + e.getMessage());
            }
        });
        return properties;
    }

    private String formatPropertyValue(PropertyDescriptor descriptor, Object bean) throws Exception {
        Class<?> type = descriptor.getPropertyType();
        Object value = descriptor.getReadMethod().invoke(bean, (Object[]) null);
        return choseFormatFunction(type).apply(value);
    }

    private Function<Object, String> choseFormatFunction(Class<?> type) {
        Class<?> elementType = determineElementType(type);
        Function<Object, String> formatter = determineElementFormatter(elementType);

        if (type.isArray()) {
            return new ArrayFormatter(formatter)::formatArray;
        } else {
            return formatter;
        }
    }

    private Function<Object, String> determineElementFormatter(Class<?> type) {
        if (Byte.class == type) {
            return this.byteFormatter;
        } else if (Short.class == type) {
            return this.shortFormatter;
        } else if (Integer.class == type) {
            return this.intFormatter;
        } else if (Long.class == type) {
            return this.longFormatter;
        } else if (Enum.class == type) {
            return (value) -> ((Enum) value).name();
        } else {
            return (value) -> value.toString();
        }
    }

    private Class<?> determineElementType(Class<?> type) {
        if (byte.class == type || Byte.class == type) {
            return Byte.class;
        } else if (short.class == type || Short.class == type) {
            return Short.class;
        } else if (int.class == type || Integer.class == type) {
            return Integer.class;
        } else if (long.class == type || Long.class == type) {
            return Long.class;
        } else if (type.isEnum()) {
            return Enum.class;
        } else if (type.isArray()) {
            return type.getComponentType();
        } else {
            return Object.class;
        }
    }

    @AllArgsConstructor
    private static class ArrayFormatter {
        private final Function<Object, String> formatter;

        private String formatArray(Object array) {
            int length = Array.getLength(array);
            StringBuffer buffer = new StringBuffer("[ ");
            for (int i = 0; i < length; i++) {
                buffer.append(formatter.apply(Array.get(array, i)));
                if (length - i > 1) {
                    buffer.append(", ");
                }
            }
            return buffer.append(" ]").toString();
        }
    }
}
