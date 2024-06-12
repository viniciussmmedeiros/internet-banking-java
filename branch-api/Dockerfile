FROM maven AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]