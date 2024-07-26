FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} leaguegenerator.jar
ENTRYPOINT ["java","-jar","/leaguegenerator.jar"]