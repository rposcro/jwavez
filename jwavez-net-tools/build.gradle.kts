plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.java)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.springBoot)
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
            artifactId = "jwavez-net-tools"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":jwavez-core"))
    implementation(project(":jwavez-serial"))
    implementation(libs.versions.orgSlf4j)

    implementation(libs.versions.orgSpringBootStarter)
    implementation(libs.versions.orgSpringBootJackson)
    implementation(libs.versions.orgSpringShellStarter)

    annotationProcessor(libs.versions.orgProjectLombok)
    compileOnly(libs.versions.orgProjectLombok)

    testImplementation(libs.versions.orgJunitJupiter)
    testImplementation(libs.versions.orgMockitoMockitoCore)
    testImplementation(libs.versions.orgMockitoMockitoJupiter)
    testImplementation(libs.versions.orgSpringBootStarterTest)
    testRuntimeOnly(libs.versions.orgJunitLauncher)
}