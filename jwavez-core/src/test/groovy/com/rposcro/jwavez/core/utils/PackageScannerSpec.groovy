package com.rposcro.jwavez.core.utils

import com.rposcro.jwavez.core.exceptions.CodeIntegrityException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import test.TestAnnotation
import test.TestInterface
import test.scanner.correct.TCAnnotatedTypedA
import test.scanner.correct.TCAnnotatedTypedC
import test.scanner.correct.TCA
import test.scanner.correct.TCAnnotatedTypedB
import test.scanner.correct.inner.TCAnnotatedTypedD
import test.scanner.correct.inner.TCB
import test.scanner.correct.inner.TCC
import test.scanner.incorrect1.TI1A
import test.scanner.incorrect1.TI1AnnotatedB
import test.scanner.incorrect1.TI1AnnotatedTypedA
import test.scanner.incorrect1.TI1AnnotatedTypedC
import test.scanner.incorrect1.inner.TI1AnnotatedTypedD
import test.scanner.incorrect1.inner.TI1B
import test.scanner.incorrect1.inner.TI1C
import test.scanner.incorrect2.TI2A
import test.scanner.incorrect2.TI2AnnotatedTypedA
import test.scanner.incorrect2.TI2AnnotatedTypedB
import test.scanner.incorrect2.TI2AnnotatedTypedC
import test.scanner.incorrect2.inner.TI2B
import test.scanner.incorrect2.inner.TI2C
import test.scanner.incorrect2.inner.TI2TypedD

@Unroll
class PackageScannerSpec extends Specification {

    @Shared
    def packageScanner = new PackageScanner();

    def "successful scan for annotated classes {#packagePaths}"() {
        given:
        def annotationClass = TestAnnotation.class;

        when:
        def classList = packageScanner.findAllClassesWithAnnotation(annotationClass, packagePaths.toArray(new String[0]));

        then:
        classList.size() == expectedClasses.size();
        classList.containsAll(expectedClasses);

        where:
        packagePaths                | expectedClasses
        ["test.scanner.correct"]    | [TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class, TCAnnotatedTypedD.class]
        ["test.scanner.incorrect1"] | [TI1AnnotatedTypedA.class, TI1AnnotatedTypedC.class, TI1AnnotatedB.class, TI1AnnotatedTypedD.class]
        ["test.scanner.incorrect2"] | [TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class]
    }

    def "successful scan for classified classes {#packagePaths}"() {
        given:
        def annotationClass = TestAnnotation.class;
        def expectedClassType = TestInterface.class;

        when:
        def classList = packageScanner.findAllClassifiedClasses(annotationClass, expectedClassType, packagePaths.toArray(new String[0]));

        then:
        classList.size() == expectedClasses.size();
        classList.containsAll(expectedClasses);

        where:
        packagePaths             | expectedClasses
        ["test.scanner.correct"] | [TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class, TCAnnotatedTypedD.class]
    }

    def "failed scan for classified classes {#packagePaths}"() {
        given:
        def annotationClass = TestAnnotation.class;
        def expectedClassType = TestInterface.class;

        when:
        def classList = packageScanner.findAllClassifiedClasses(annotationClass, expectedClassType, packagePaths.toArray(new String[0]));

        then:
        thrown CodeIntegrityException;

        where:
        packagePaths                | _
        ["test.scanner.incorrect2"] | _
    }
}
