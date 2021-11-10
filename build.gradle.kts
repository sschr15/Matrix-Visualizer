@file:Suppress("GradlePackageUpdate")

plugins {
    kotlin("jvm") version "1.5.31"
    java
    application
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
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

    // javafx is preferred over swing
    "shadow"(implementation("org.openjfx:javafx:17.0.1")!!)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

javafx {
    version = "17.0.1"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.web",
    )
}

application {
    mainClass.set("sschr15.matrixvisualizer.gui.JavaFX")
}
