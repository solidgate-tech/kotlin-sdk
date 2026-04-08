val ktorVersion = "1.6.8"

plugins {
    signing
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.dokka") version "1.6.21"
    `maven-publish`
    id("com.gradleup.nmcp") version "0.0.9"
}

group = "com.solidgate"
version = "0.6.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
    implementation(group = "commons-codec", name = "commons-codec", version = "1.14")
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
    testImplementation(group = "junit", name = "junit", version = "4.12")
    testImplementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    testImplementation(group = "io.ktor", name = "ktor-client-mock-jvm", version = ktorVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    publishToMavenLocal {
        dependsOn(build)
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml"))
    dependsOn(tasks.named("dokkaHtml"))
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val pomUrl = "https://github.com/solidgate-tech/kotlin-sdk"
val pomScmUrl = "https://github.com/solidgate-tech/kotlin-sdk"
val pomDesc = "Kotlin SDK for SolidGate API"
val pomScmConnection = "scm:git:git://github.com/solidgate-tech/kotlin-sdk"
val pomScmDevConnection = "scm:git:git://github.com/solidgate-tech/kotlin-sdk"

val pomLicenseName = "The Apache Software License, Version 2.0"
val pomLicenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
val pomLicenseDist = "repo"

val pomDeveloperId = "Gaidoba"
val pomDeveloperName = "Yuri Gaidoba"

publishing {
    publications {
        create<MavenPublication>("lib") {
            groupId = "com.solidgate"
            artifactId = "solidgate-api-sdk"
            version = version

            from(components["java"])
            artifact(dokkaJar)
            artifact(sourcesJar)

            pom.withXml {
                asNode().apply {
                    appendNode("description", pomDesc)
                    appendNode("name", rootProject.name)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", pomDeveloperId)
                        appendNode("name", pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                        appendNode("connection", pomScmConnection)
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

val centralPortalUsername: String by project
val centralPortalPassword: String by project

nmcp {
    publish("lib") {
        username.set(centralPortalUsername)
        password.set(centralPortalPassword)
        publicationType.set("AUTOMATIC")
    }
}

signing {
    sign(publishing.publications["lib"])
}
