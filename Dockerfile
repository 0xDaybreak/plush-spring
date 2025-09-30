FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY pom.xml mvnw* ./
COPY .mvn .mvn

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/plush-spring-0.0.1-SNAPSHOT.jar app.jar

# Expose the app port + JMX port
EXPOSE 8080
EXPOSE 9010

# JVM options including JMX
ENV JAVA_OPTS="-Xms128m -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/heapdump.hprof \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9010 \
-Dcom.sun.management.jmxremote.rmi.port=9010 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-Djava.rmi.server.hostname=0.0.0.0"

# Run the app with the JVM options
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
