import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.0"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.spring") version "1.4.10"
    kotlin("plugin.jpa") version "1.4.10"
}

group = "cn.wycode"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.h2database:h2")
    implementation("io.springfox:springfox-swagger2:2.9.0") //Swagger JSON generate
    implementation("us.codecraft:webmagic-extension:0.7.3") {
        exclude(group = "redis.clients", module = "jedis")
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
    implementation("com.baidu.aip:java-sdk:4.6.1")
    implementation("com.aliyun.oss:aliyun-sdk-oss:2.8.3")
    implementation("com.aliyun.openservices:aliyun-log:0.6.32")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

