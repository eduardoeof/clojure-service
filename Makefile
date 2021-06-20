build:
		docker build --tag clojure-service:latest .

run:
		docker run --rm -p 8080:8080 clojure-service

up:
		docker-compose up

health:
		curl localhost:8080/api/health

mongodb:
	mongo --host localhost --port 27017
