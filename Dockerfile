# Step 1: Use a lightweight JDK image to build & run
FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Copy Maven/Gradle wrapper and pom/build files first (for layer caching if needed)
COPY pom.xml mvnw* ./
COPY .mvn .mvn

# Download dependencies (will be cached if pom.xml hasn’t changed)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Step 2: Run stage with smaller JDK/JRE image
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the fat jar from builder stage
COPY --from=builder /app/target/plush-spring-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Add JVM options for memory tuning (you can override in k8s later)
ENV JAVA_OPTS="-Xms128m -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/heapdump.hprof"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]