plugins {
	java
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.flywaydb.flyway") version "9.16.3"
}

group = "com.eshop"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
	implementation("org.hibernate.orm:hibernate-core")
	implementation ("org.flywaydb:flyway-core:9.16.3")
	implementation("jakarta.transaction:jakarta.transaction-api")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	//developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	compileOnly ("org.projectlombok:lombok:1.18.32")
	annotationProcessor("org.projectlombok:lombok:1.18.32")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("jakarta.validation:jakarta.validation-api:3.0.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("org.junit.jupiter:junit-jupiter:5.7.1")
	testImplementation ("org.mockito:mockito-core:4.11.0")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.5")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
