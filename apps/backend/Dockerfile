# =============================================================================
# omiiCARE_QA backend — multi-stage production image.
# Stage 1 builds the Spring Boot fat jar with the reactor; stage 2 runs it on a
# slim JRE as a non-root user. Built from the repository root:
#   docker build -f apps/backend/Dockerfile -t omiicare-qa-backend .
# =============================================================================

# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache dependencies: copy the reactor + module POMs first.
COPY pom.xml ./
COPY apps/backend/pom.xml apps/backend/
COPY automation/pom.xml automation/
RUN mvn -B -ntp -pl apps/backend -am dependency:go-offline || true

# Copy sources and build only the backend module (skip tests in the image build;
# tests run in CI).
COPY apps/backend/src apps/backend/src
RUN mvn -B -ntp -pl apps/backend -am -DskipTests package

# --- Runtime stage ---
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Run as a non-root user.
RUN groupadd --system omii && useradd --system --gid omii --home /app omii

COPY --from=build /workspace/apps/backend/target/omiicare-qa-backend.jar app.jar
RUN chown -R omii:omii /app
USER omii

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
