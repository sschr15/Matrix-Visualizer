@file:Suppress("GradlePackageUpdate")

plugins {
    kotlin("jvm") version "1.5.31"
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "sschr15"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    "shadow"(implementation(kotlin("stdlib"))!!)
    "shadow"(implementation(kotlin("stdlib-jdk8"))!!)

    // jfreesvg for creating the matrices
    "shadow"(implementation("org.jfree:org.jfree.svg:5.0.2")!!)
    // and batik transcoder converting the svg to a png or other formats
    "shadow"(implementation("org.apache.xmlgraphics:batik-transcoder:1.14")!!)
    "shadow"(implementation("org.apache.xmlgraphics:batik-codec:1.14")!!)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        val thiz = this as JavaToolchainSpec
        thiz.languageVersion.set(JavaLanguageVersion.of(16))
    }
}

application {
    mainClass.set("sschr15.matrixvisualizer.MainKt")
}
