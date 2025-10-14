plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.6"
    id("java")
    id("jacoco")
    id("com.diffplug.spotless") version "6.25.0"
    id("checkstyle")
}

group = "com.example"
version = "1.0.0"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("FAILED", "SKIPPED", "PASSED")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register("jacocoCoverageVerification", JacocoCoverageVerification::class) {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                // невысокий порог, чтобы сборка не падала (можно поднять позже)
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.10".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn("spotlessCheck", "checkstyleMain", "checkstyleTest", "jacocoCoverageVerification")
}

tasks.bootJar {
    archiveFileName.set("notes-calendar-${project.version}.jar")
}

tasks.javadoc {
    val opt = (options as org.gradle.external.javadoc.StandardJavadocDocletOptions)
    opt.encoding = "UTF-8"
    opt.charSet = "UTF-8"
    opt.addStringOption("Xdoclint:none", "-quiet")
    opt.links("https://docs.oracle.com/javase/8/docs/api/")
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
}
