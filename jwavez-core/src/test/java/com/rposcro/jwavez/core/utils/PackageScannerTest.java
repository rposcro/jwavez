package com.rposcro.jwavez.core.utils;

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException;
import com.rposcro.jwavez.core.utils.test.TestAnnotation;
import com.rposcro.jwavez.core.utils.test.TestBaseClass;
import com.rposcro.jwavez.core.utils.test.package1.AnnotatedC11;
import com.rposcro.jwavez.core.utils.test.package1.AnnotatedC12;
import com.rposcro.jwavez.core.utils.test.package2.AnnotatedC21;
import com.rposcro.jwavez.core.utils.test.package3.AnnotatedC31;
import com.rposcro.jwavez.core.utils.test.package3.PureC31;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackageScannerTest {

    @Test
    public void findsAllClassesWithAnnotation() {
        final PackageScanner scanner = new PackageScanner();
        final Set<Class<?>> expectedClasses = Stream.of(AnnotatedC11.class, AnnotatedC12.class, AnnotatedC21.class, AnnotatedC31.class)
                .collect(Collectors.toSet());

        Set<Class<?>> foundClasses = scanner.findAllClassesWithAnnotation(
                TestAnnotation.class,
                "com.rposcro.jwavez.core.utils.test.package1",
                "com.rposcro.jwavez.core.utils.test.package2",
                "com.rposcro.jwavez.core.utils.test.package3");

        assertEquals(expectedClasses.size(), foundClasses.size());
        assertTrue(expectedClasses.containsAll(foundClasses));
        assertTrue(foundClasses.containsAll(expectedClasses));
    }

    @Test
    public void findsAllClassesOfType() {
        final PackageScanner scanner = new PackageScanner();
        final Set<Class<?>> expectedClasses = Stream.of(AnnotatedC11.class, AnnotatedC12.class, AnnotatedC31.class, PureC31.class)
                .collect(Collectors.toSet());

        Set<Class<? extends TestBaseClass>> foundClasses = scanner.findAllClassesOfType(TestBaseClass.class,
                "com.rposcro.jwavez.core.utils.test.package1",
                "com.rposcro.jwavez.core.utils.test.package2",
                "com.rposcro.jwavez.core.utils.test.package3");

        assertEquals(expectedClasses.size(), foundClasses.size());
        assertTrue(expectedClasses.containsAll(foundClasses));
        assertTrue(foundClasses.containsAll(expectedClasses));
    }

    @Test
    public void findsAllClassifiedClasses() {
        final PackageScanner scanner = new PackageScanner();
        final Set<Class<?>> expectedClasses = Stream.of(AnnotatedC11.class, AnnotatedC12.class)
                .collect(Collectors.toSet());

        Set<Class<? extends TestBaseClass>> foundClasses = scanner.findAllClassifiedClasses(
                TestAnnotation.class,
                TestBaseClass.class,
                "com.rposcro.jwavez.core.utils.test.package1");

        assertEquals(expectedClasses.size(), foundClasses.size());
        assertTrue(expectedClasses.containsAll(foundClasses));
        assertTrue(foundClasses.containsAll(expectedClasses));
    }

    @Test
    public void failsWhenMissingAnnotation() {
        final PackageScanner scanner = new PackageScanner();

        assertThrows(CodeIntegrityException.class, () -> scanner.findAllClassifiedClasses(
                TestAnnotation.class,
                TestBaseClass.class,
                "com.rposcro.jwavez.core.utils.test.package3"));
    }
}
