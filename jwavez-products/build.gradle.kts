plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.java)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.lombok)
}

val jwavezVersion = providers.gradleProperty("jwavez.version")
val jwavezGroup = providers.gradleProperty("jwavez.group")

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = jwavezGroup.getOrNull()
            version = jwavezVersion.get()
            artifactId = "jwavez-products"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":jwavez-core"))
    implementation(libs.versions.orgSlf4j)

    implementation(libs.versions.orgSpringBootStarter)
    implementation(libs.versions.orgSpringBootJackson)

//    compileOnly(libs.versions.orgProjectLombok)
//    annotationProcessor(libs.versions.orgProjectLombok)

    testImplementation(libs.versions.orgJunitJupiter)
    testImplementation(libs.versions.orgMockitoMockitoCore)
    testImplementation(libs.versions.orgMockitoMockitoJupiter)
    testImplementation(libs.versions.orgSpringBootStarterTest)

    testRuntimeOnly(libs.versions.orgJunitLauncher)
//    testCompileOnly(libs.versions.orgProjectLombok)
//    testAnnotationProcessor(libs.versions.orgProjectLombok)
}