FROM openjdk:8 as build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-ea-28-jdk-slim
COPY --from=build /target/SCM-0.0.1-SNAPSHOT.jar SCM.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar","SCM.jar" ]
