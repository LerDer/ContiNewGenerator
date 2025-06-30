plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "top.continew"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://jetbrains.bintray.com/intellij-third-party-dependencies")
    }
    maven {
        url = uri("https://jetbrains.bintray.com/intellij-plugin-service")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
//    version.set("2023.2.6")
//    type.set("IC") // Target IDE Platform
    intellij.updateSinceUntilBuild = false
    localPath.set("/Users/lww/Downloads/ideaJar/ideaIC-2022.3")
    plugins.set(
        listOf(
            "com.intellij.java",
            "org.jetbrains.kotlin",
//            "DatabaseTools",
//            "uml"
        )
    )
//    pluginName.set("ContiNewGenerator")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
//    implementation("com.oracl:ojdbc6:11.2.0.3")
//    implementation("com.h2database:h2:2.2.224")
//    implementation("com.ibm.db2:jcc:11.5.8.0")
//    implementation("cn.hutool:hutool-all:5.8.26")
//    implementation("org.springframework.boot:spring-boot-starter-web:2.3.4.RELEASE")
//    implementation("com.alibaba:druid-spring-boot-starter:1.2.23")
//    implementation ("com.baomidou:mybatis-plus-boot-starter:3.4.2")
    implementation("com.baomidou:mybatis-plus-generator:3.4.1")
    implementation("org.freemarker:freemarker:2.3.28")
    implementation("com.baomidou:mybatis-plus:3.5.3.1")
    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("com.alibaba:druid:1.2.16")
    implementation("org.springframework:spring-jdbc:5.3.39")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("log4j:log4j:1.2.17")
    implementation("com.alibaba:fastjson:1.2.83")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
    }

}
