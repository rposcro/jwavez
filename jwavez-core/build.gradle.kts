plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.javaLibrary)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.lombok)
}

val jwavezVersion = providers.gradleProperty("jwavez.version")
val jwavezGroup = providers.gradleProperty("jwavez.group")

repositories {
    mavenLocal()
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = jwavezGroup.getOrNull()
            version = jwavezVersion.get()
            artifactId = "jwavez-core"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.versions.orgReflections)
    implementation(libs.versions.orgSlf4j)

//    compileOnly(libs.versions.orgProjectLombok)
//    annotationProcessor(libs.versions.orgProjectLombok)

    testImplementation(libs.versions.orgJunitJupiter)
    testImplementation(libs.versions.orgMockitoMockitoCore)
    testImplementation(libs.versions.orgMockitoMockitoJupiter)

    testRuntimeOnly(libs.versions.orgJunitLauncher)
//    testCompileOnly(libs.versions.orgProjectLombok)
//    testAnnotationProcessor(libs.versions.orgProjectLombok)
}