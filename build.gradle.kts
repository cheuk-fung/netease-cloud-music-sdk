plugins {
    kotlin("jvm") version "1.2.41"
    id("java-library")
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.reactivex.rxjava2:rxkotlin:2.2.0")
    implementation("org.slf4j:slf4j-api:1.7.25")

    val retrofitVersion = "2.4.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-jackson:$retrofitVersion")

    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")

    val junitVersion = "5.2.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
