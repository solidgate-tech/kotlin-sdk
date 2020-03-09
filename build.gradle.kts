val ktorVersion = "1.3.0"

plugins {
    kotlin("jvm") version "1.3.70"
}

group = "com.solidgate"
version = "0.0.1"

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
    implementation(group = "commons-codec", name = "commons-codec", version = "1.14")
    testImplementation(group = "junit", name = "junit", version = "4.12")
    testImplementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    testImplementation(group= "io.ktor", name = "ktor-client-mock-jvm", version = ktorVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
