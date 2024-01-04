FROM maven:3.8.3-openjdk-17 AS build
COPY . .


FROM openjdk:17-ea-28-jdk-slim
COPY --from=build /target/SCM-0.0.1-SNAPSHOT.jar SCM.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","SCM.jar" ]
