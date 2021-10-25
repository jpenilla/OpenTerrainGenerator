pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    includeBuild("build-logic")
}

rootProject.name = "OpenTerrainGenerator"

include(
    "common:common-util",
    "common:common-config",
    "common:common-customobject",
    "common:common-generator",
    "common:common-core",
    "platforms:forge",
    "platforms:fabric",
    "platforms:paper"
)
