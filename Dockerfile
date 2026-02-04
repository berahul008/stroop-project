FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/stroop-web-0.1.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
