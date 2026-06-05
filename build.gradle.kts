plugins {
    alias(libs.plugins.javaLibrary)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
