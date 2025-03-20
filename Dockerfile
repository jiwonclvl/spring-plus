FROM openjdk:17-jdk
COPY build/libs/expert-0.0.1-SNAPSHOT.jar /project.jar
ENTRYPOINT ["java", "-jar", "/project.jar"]