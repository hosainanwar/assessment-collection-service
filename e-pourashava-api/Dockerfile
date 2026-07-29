FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/e-pourashava-api-*.jar app.jar

EXPOSE 9080

ENTRYPOINT ["java", "-jar", "app.jar"]
