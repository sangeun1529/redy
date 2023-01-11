group = "io.foreshore.redy"
version = "0.0.1-SNAPSHOT"

val nexusUser: String by project
val nexusPassword: String by project
val nexus: String by project
val nexusSnapshot: String by project
val nexusRelease: String by project

// - CI 를 위한 기본설정값 ( default tool = nexus )
val repo: String = System.getenv("CI_NEXUS") ?: nexus
val repoSnapshot: String = System.getenv("CI_NEXUS_SNAPSHOT") ?: nexusSnapshot
val repoRelease: String = System.getenv("CI_NEXUS_RELEASE") ?: nexusRelease
val repoUser: String = System.getenv("CI_NEXUS_USER") ?: nexusUser
val repoPassword: String = System.getenv("CI_NEXUS_PASSWORD") ?: nexusPassword

// -P 옵션을 이용한 profile 설정
var profile = when {
    project.hasProperty("prod") -> "prod"
    project.hasProperty("dev") -> "dev"
    else -> "local"
}
println("* Build Profile = $profile")

repositories {
    maven {
        url = uri(repo)
        credentials {
            username = repoUser
            password = repoPassword
        }
    }
}

plugins {
    val kotlinVersion = "1.6.10"
    id("org.springframework.boot") version "2.4.13"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.github.node-gradle.node") version "3.2.0" // for frontend build.
    id("org.sonarqube") version "3.4.0.2513"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    `maven-publish`
    jacoco
    idea
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat") // implementation { exclude() } 로는 제외 되지가 않는다.
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperClass")
}

noArg {
    annotation("com.obvioustraverse.miska.annotation.NoArg")
}

node {
    download.set(false)
    workDir.set(file("./frontend"))
    npmWorkDir.set(file("./frontend"))
    nodeProjectDir.set(file("./frontend"))
    npmInstallCommand.set("install")
}

springBoot {
    buildInfo()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sonarqube {
    properties {
        property("sonar.projectKey", project.name)
    }
}

dependencies {
    api(kotlin("reflect"))
    api(kotlin("stdlib-jdk8"))

    kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.ehcache:ehcache")
    implementation("javax.cache:cache-api")
    implementation("org.flywaydb:flyway-core")
    implementation("com.querydsl:querydsl-jpa")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("com.obvioustraverse:miska:0.0.14-SNAPSHOT") { isChanging = true } // MISKA

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation(kotlin("test-junit5"))

    // dependency by profile
    when (profile) {
        "local" -> {
            runtimeOnly("com.h2database:h2")
            runtimeOnly("org.lazyluke:log4jdbc-remix:0.2.7")
        }
        else -> {
            runtimeOnly("org.postgresql:postgresql:42.2.11")
        }
    }
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.processResources {
    dependsOn("cp_index")
}

tasks.create("build-frontend") { // frontend build
    dependsOn(tasks.npmInstall)
    dependsOn("npm_run_build")
}

tasks.create<Copy>("cp_index") {
    dependsOn("build-frontend")
    from("src/main/resources/static/index.html")
    into("src/main/resources/templates/")
}

tasks.create("clean_frontend") {
    delete("src/main/resources/static")
    delete("src/main/resources/templates/index.html")
}

tasks.clean {
    finalizedBy("clean_frontend")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // 이게 없으면 jacocoTestReport 가 실행 되지 않는다.
    finalizedBy(tasks.asciidoctor)
}

tasks.asciidoctor {
    setBaseDir(sourceDir)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.bootJar {
    manifest {
        attributes("Implementation-Version" to project.version)
        attributes("Implementation-Title" to project.name)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.bootJar.get())
        }
    }
    repositories {
        maven {
            credentials {
                username = repoUser
                password = repoPassword
            }
            url = uri(if (project.version.toString().contains("SNAPSHOT", true)) repoSnapshot else repoRelease)
        }
    }
}
