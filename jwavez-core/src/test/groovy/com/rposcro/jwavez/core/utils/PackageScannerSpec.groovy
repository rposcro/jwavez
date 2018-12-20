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

    def "successful scan for all classes {#packagePath, #recursive}"() {
        when:
        def classList = packageScanner.findAllClasses(packagePath, recursive);

        then:
        classList.size() == expectedClasses.size();
        classList.containsAll(expectedClasses);

        where:
        packagePath                | recursive | expectedClasses
        "test.scanner.correct"     | false     | [ TCA.class, TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class ]
        "test.scanner.correct"     | true      | [ TCA.class, TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class, TCB.class, TCC.class, TCAnnotatedTypedD.class ]
        "test.scanner.incorrect1"  | false     | [ TI1A.class, TI1AnnotatedTypedA.class, TI1AnnotatedTypedC.class, TI1AnnotatedB.class ]
        "test.scanner.incorrect1"  | true      | [ TI1A.class, TI1AnnotatedTypedA.class, TI1AnnotatedTypedC.class, TI1AnnotatedB.class, TI1B.class, TI1C.class, TI1AnnotatedTypedD.class ]
        "test.scanner.incorrect2"  | false     | [ TI2A.class, TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class ]
        "test.scanner.incorrect2"  | true      | [ TI2A.class, TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class, TI2B.class, TI2C.class, TI2TypedD.class ]
    }

    def "successful scan for annotated classes {#packagePath, #recursive}"() {
        given:
        def annotationClass = TestAnnotation.class;

        when:
        def classList = packageScanner.findAllClassesWithAnnotation(packagePath, recursive, annotationClass);

        then:
        classList.size() == expectedClasses.size();
        classList.containsAll(expectedClasses);

        where:
        packagePath                | recursive | expectedClasses
        "test.scanner.correct"     | false     | [ TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class ]
        "test.scanner.correct"     | true      | [ TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class, TCAnnotatedTypedD.class ]
        "test.scanner.incorrect1"  | false     | [ TI1AnnotatedTypedA.class, TI1AnnotatedTypedC.class, TI1AnnotatedB.class ]
        "test.scanner.incorrect1"  | true      | [ TI1AnnotatedTypedA.class, TI1AnnotatedTypedC.class, TI1AnnotatedB.class, TI1AnnotatedTypedD.class ]
        "test.scanner.incorrect2"  | false     | [ TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class ]
        "test.scanner.incorrect2"  | true      | [ TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class ]
    }

    def "successful scan for classified classes {#packagePath, #recursive}"() {
        given:
        def annotationClass = TestAnnotation.class;
        def expectedClassType = TestInterface.class;

        when:
        def classList = packageScanner.findAllClassifiedClasses(packagePath, recursive, annotationClass, expectedClassType);

        then:
        classList.size() == expectedClasses.size();
        classList.containsAll(expectedClasses);

        where:
        packagePath                | recursive | expectedClasses
        "test.scanner.correct"     | false     | [ TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class ]
        "test.scanner.correct"     | true      | [ TCAnnotatedTypedA.class, TCAnnotatedTypedB.class, TCAnnotatedTypedC.class, TCAnnotatedTypedD.class ]
        "test.scanner.incorrect2"  | false     | [ TI2AnnotatedTypedA.class, TI2AnnotatedTypedB.class, TI2AnnotatedTypedC.class ]
    }

    def "failed scan for classified classes {#packagePath, #recursive}"() {
        given:
        def annotationClass = TestAnnotation.class;
        def expectedClassType = TestInterface.class;

        when:
        def classList = packageScanner.findAllClassifiedClasses(packagePath, recursive, annotationClass, expectedClassType);

        then:
        thrown CodeIntegrityException;

        where:
        packagePath                | recursive
        "test.scanner.incorrect1"  | false
        "test.scanner.incorrect1"  | true
        "test.scanner.incorrect2"  | true
    }
}
