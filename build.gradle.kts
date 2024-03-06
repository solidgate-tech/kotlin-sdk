val ktorVersion = "1.3.0"

plugins {
    signing
    kotlin("jvm") version "1.3.70"
    id("org.jetbrains.dokka") version "0.9.17"
    `maven-publish`
}

group = "com.solidgate"
version = "0.5.1"

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
    implementation(group = "commons-codec", name = "commons-codec", version = "1.14")
    implementation(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    implementation(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
    compile(group = "io.ktor", name = "ktor-client-core-jvm", version = ktorVersion)
    compile(group = "io.ktor", name = "ktor-client-apache", version = ktorVersion)
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
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
        moduleName = rootProject.name
    }

    publishToMavenLocal {
        dependsOn(build)
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
    dependsOn(tasks.dokka)
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

val ossrhUsername: String by project
val ossrhPassword: String by project

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
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["lib"])
}
