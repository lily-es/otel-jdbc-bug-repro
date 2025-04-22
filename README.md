# Repro scenario for OTel JDBC Library bug
This is a repro scenario for a bug involving OpenTelemetry JDBC and HikariCP library instrumentation when inserting rows or creating tables with Jetbrains [Exposed](https://github.com/JetBrains/Exposed) 

### Requirements
JDK 22

### Steps
1. Run tests with `./gradlew test`
2. Observe that SELECT does not fail, but CREATE TABLE and INSERT fail with an NPE