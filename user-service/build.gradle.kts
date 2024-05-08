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
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation ("org.hibernate.orm:hibernate-core:6.4.4.Final")
	implementation ("org.hibernate.orm:hibernate-core")
	implementation ("org.flywaydb:flyway-core:9.16.3")
	implementation ("jakarta.transaction:jakarta.transaction-api")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("io.jsonwebtoken:jjwt-api:0.11.1")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.1")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.1")
	implementation("org.mindrot:jbcrypt:0.4")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	//developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")

	compileOnly ("org.projectlombok:lombok:1.18.32")
	annotationProcessor ("org.projectlombok:lombok:1.18.32")
	runtimeOnly("org.postgresql:postgresql")
	implementation("jakarta.validation:jakarta.validation-api:3.0.0")


}
dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.5")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
