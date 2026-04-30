FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

COPY module-app/build/libs/module-app-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
