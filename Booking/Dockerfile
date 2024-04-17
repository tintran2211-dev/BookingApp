#Build stage
FROM openjdk:21-slim AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

#Runtime stage
FROM openjdk:21-slim AS runtime
WORKDIR /app
COPY --from=build /app/target/Booking-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
ENTRYPOINT ["sh","-c","java -jar Booking-0.0.1-SNAPSHOT.jar"]