import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	kotlin("plugin.jpa") version "1.7.22"
	id("com.google.cloud.tools.jib") version "3.3.1"
	id("org.jetbrains.kotlin.kapt") version "1.5.31"
	id("io.freefair.lombok") version "6.2.0"
}

group = "it.polito.wa2.g13"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.hibernate.validator:hibernate-validator")
	testImplementation ("org.testcontainers:junit-jupiter:1.16.3")
	testImplementation("org.testcontainers:postgresql:1.16.3")
	implementation ("com.google.code.gson:gson:2.8.9")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	compileOnly("org.projectlombok:lombok:1.18.20")
	annotationProcessor("org.projectlombok:lombok:1.18.20")
	implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
	implementation ("org.keycloak:keycloak-admin-client:21.1.1")
	implementation ("org.keycloak:keycloak-core:21.1.1")

	implementation ("org.springframework.boot:spring-boot-starter-aop")
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	implementation ("io.micrometer:micrometer-registry-prometheus")
	implementation ("io.micrometer:micrometer-tracing-bridge-brave")
	implementation ("io.zipkin.reporter2:zipkin-reporter-brave")
	implementation ("com.github.loki4j:loki-logback-appender:1.4.0-rc2")

	implementation("io.github.microutils:kotlin-logging:2.0.11")
	implementation("org.slf4j:slf4j-api:1.7.32")
	implementation("ch.qos.logback:logback-classic:1.2.9")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.16.3")
	}
}