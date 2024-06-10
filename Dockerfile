FROM eclipse-temurin:17
ADD target/onoff-0.1-SNAPSHOT.jar onoff.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "onoff.jar"]