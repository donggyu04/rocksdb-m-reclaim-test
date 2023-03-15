plugins {
    java
    kotlin("jvm") version "1.8.10"
}

group = "org.rocksdb-example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes(mapOf("Main-Class" to "org.rocksdb.example.RocksDbProcessorKt"))
    }

    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("com.github.oshi:oshi-core:6.4.0")
    implementation("org.rocksdb:rocksdbjni:7.10.2")

    implementation("org.slf4j:slf4j-nop:2.0.5")
}
