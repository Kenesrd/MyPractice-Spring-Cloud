FROM eclipse-temurin:17.0.5_8-jre-focal
EXPOSE 8080
ADD ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]



