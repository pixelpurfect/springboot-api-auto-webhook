
---

    #### 2. `Dockerfile`
    
    ```dockerfile
    FROM eclipse-temurin:17-jdk-alpine
    VOLUME /tmp
    COPY release/app.jar app.jar
    ENTRYPOINT ["java","-jar","/app.jar"]
    