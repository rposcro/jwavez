pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "jwavez"

include ("jwavez-core", "jwavez-serial", "jwavez-examples", "jwavez-products", "jwavez-net-tools")