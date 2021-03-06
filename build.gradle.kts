import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.50"
    id("java-library")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.apache.commons:commons-lang3:3.7")
    implementation("commons-codec:commons-codec:1.11")
    implementation("net.bytebuddy:byte-buddy:1.8.12")
    implementation("com.google.code.gson:gson:2.8.5")

    val retrofitVersion = "2.4.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    val okhttpVersion = "3.10.0"
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okhttpVersion")

    val junitVersion = "5.2.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testImplementation("org.assertj:assertj-core:3.10.0")
    testImplementation("org.mockito:mockito-core:2.18.3")
    testImplementation("org.skyscreamer:jsonassert:1.2.3")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
