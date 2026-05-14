plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.javaLibrary)
    alias(libs.plugins.mavenPublish)
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
            artifactId = "jwavez-examples"
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

    annotationProcessor(libs.versions.orgProjectLombok)
    compileOnly(libs.versions.orgProjectLombok)
}