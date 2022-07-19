import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("org.jlleitschuh.gradle.ktlint-idea") version "10.3.0"

    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

val javaVersion = JavaVersion.VERSION_11

java.sourceCompatibility = javaVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin") //
    implementation("org.jetbrains.kotlin:kotlin-reflect") //
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") //
    implementation("junit:junit:4.13.1") //
    implementation("com.h2database:h2") // runtime -> implementation

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    bootRun {
        dependsOn(ktlintFormat)
        args("--spring.profiles.active=local")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = javaVersion.majorVersion
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
        workerMaxHeapSize.set("1024m")
    }
}

allOpen {
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.Entity")
}
