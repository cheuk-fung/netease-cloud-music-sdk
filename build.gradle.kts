plugins {
    kotlin("jvm") version "1.2.41"
    id("java-library")
}

dependencies {
    val retrofitVersion = "2.4.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
}
