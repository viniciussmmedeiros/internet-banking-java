FROM maven AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM amazoncorretto:17

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

# COPY ./auth-api-0.0.1-SNAPSHOT.jar /app/auth-api.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]