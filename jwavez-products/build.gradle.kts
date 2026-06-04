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
    implementation(libs.orgSlf4j)

    implementation(libs.orgSpringBootStarter)
    implementation(libs.orgSpringBootJackson)

    testImplementation(libs.orgJunitJupiter)
    testImplementation(libs.orgMockitoMockitoCore)
    testImplementation(libs.orgMockitoMockitoJupiter)
    testImplementation(libs.orgSpringBootStarterTest)

    testRuntimeOnly(libs.orgJunitLauncher)
}