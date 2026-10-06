# ---- Build stage: compile the Spring Boot fat jar with the Gradle wrapper ----
FROM eclipse-temurin:11-jdk-jammy AS build
WORKDIR /app

# Copy build definition first so dependency resolution is cached independently
# of source changes.
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

# Now the sources; build the executable jar (tests are run in CI, skipped here).
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage: slim JRE, non-root, writable data volume ----
FROM eclipse-temurin:11-jre-jammy
WORKDIR /app

# Run as a non-root user and pre-create the config folder so a mounted named
# volume inherits the right ownership on first creation.
RUN groupadd -r spring && useradd -r -g spring spring \
    && mkdir -p /data/configurations \
    && chown -R spring:spring /data

COPY --from=build /app/build/libs/*.jar app.jar

# The service writes YAML configurations here; mount a volume to persist them.
ENV FILES_UPLOAD_FOLDER=/data/configurations/
# Port Spring Boot listens on (server.port); override with -e SERVER_PORT=...
ENV SERVER_PORT=8090

EXPOSE 8090
USER spring
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
