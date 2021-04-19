FROM clojure:lein 
MAINTAINER Eduardo Ferreira <eduardoofe@gmail.com>

RUN mkdir /clojure-service
WORKDIR /clojure-service
COPY . /clojure-service

RUN lein deps
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

EXPOSE 8080

CMD ["java", "-jar", "app-standalone.jar"]
