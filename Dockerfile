FROM openjdk:8 as build
COPY . .
EXPOSE 8080  
FROM openjdk:8-jre-slim
COPY --from=build /target/SCM-0.0.1-SNAPSHOT.jar SCM-0.0.1-SNAPSHOT.jar 
ENTRYPOINT ["java","-jar","/SCM-0.0.1-SNAPSHOT.jar"] 
