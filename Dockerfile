FROM eclipse-temurin:17
ADD target/onoff-1.1-SNAPSHOT.jar onoff.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "onoff.jar"]

#ENV SPRING_PROFILES_ACTIVE=prod TODO should be set here?
