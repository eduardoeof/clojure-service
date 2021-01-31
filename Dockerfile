FROM openjdk:11-slim
MAINTAINER Your Name <you@example.com>

ADD target/clojure-service-0.0.1-SNAPSHOT-standalone.jar /clojure-service/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/clojure-service/app.jar"]
