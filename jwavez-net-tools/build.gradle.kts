plugins {
    alias(libs.plugins.springBoot)
    alias(libs.plugins.javaLibrary)
    alias(libs.plugins.mavenPublish)
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
            artifactId = "jwavez-net-tools"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":jwavez-core"))
    implementation(project(":jwavez-products"))
    implementation(project(":jwavez-serial"))
    implementation(libs.orgSlf4j)
    implementation(libs.commonsCli)
    implementation(libs.orgJLineBundle)

    implementation(libs.orgSpringBootStarter)
    implementation(libs.orgSpringBootJackson)
    implementation(libs.orgSpringShellStarter)
    implementation(libs.orgSpringShellJLine)

    testImplementation(libs.orgJunitJupiter)
    testImplementation(libs.orgMockitoMockitoCore)
    testImplementation(libs.orgMockitoMockitoJupiter)
    testImplementation(libs.orgSpringBootStarterTest)

    testRuntimeOnly(libs.orgJunitLauncher)
}