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
            artifactId = "jwavez-serial"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":jwavez-core"))
    implementation(libs.comNeuronrobotics)
    implementation(libs.comFazecast)
    implementation(libs.orgSlf4j)

    testImplementation(libs.orgJunitJupiter)
    testImplementation(libs.orgMockitoMockitoCore)
    testImplementation(libs.orgMockitoMockitoJupiter)

    testRuntimeOnly(libs.orgJunitLauncher)
}