FROM openjdk
COPY ./target/*.jar spring-application.jar
ENTRYPOINT ["java","-jar","/spring-Application.jar"]