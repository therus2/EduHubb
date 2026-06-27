FROM maven:3.8-eclipse-temurin-11 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:11-jre-jammy
WORKDIR /app

RUN groupadd -r eduhab && useradd -r -g eduhab eduhab

COPY --from=build /app/target/EduHubBackend-*.jar app.jar
RUN mkdir -p logs && chown -R eduhab:eduhab /app

USER eduhab

EXPOSE 8067

ENV SERVER_PORT=8067

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
