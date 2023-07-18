plugins {
    kotlin("jvm") version "1.8.20"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.6"
    id("org.sonarqube") version "3.3"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://m2.dv8tion.net/releases")
        }

        google()
        gradlePluginPortal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://nexus.twelveiterations.com/repository/maven-proxy/") }

        maven {
            url = uri("https://libraries.minecraft.net")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    sonarqube {
        properties {
            property("sonar.sources", "src")
        }
    }
}

//idea { TODO How is this plugin literally written in Kotlin, by the Kotlin developers, but does not support Kotlin run configurations? What the fuck?
//    project {
//        settings {
//            runConfigurations {
//                create("Run Client", Application::class.java) {
//                    mainClass = "net.eiradir.client.EiradirClientKt"
//                    moduleName = idea.module.name.replace(' ', '_') + ".engine.main"
//                    workingDirectory = "run"
//                }
//                create("Run Server", Application::class.java) {
//                    mainClass = "net.eiradir.server.EiradirServerKt"
//                    moduleName = idea.module.name.replace(' ', '_') + ".engine.main"
//                    workingDirectory = "run"
//                }
//            }
//        }
//    }
//}