FROM openjdk:21-slim AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
FROM openjdk:21-slim AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]