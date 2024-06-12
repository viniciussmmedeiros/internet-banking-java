FROM maven as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM amazoncorretto:17

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]