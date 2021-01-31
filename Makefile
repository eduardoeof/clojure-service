build:
		lein uberjar
		docker build --tag clojure-service:latest .

run:
	docker run --rm -p 8080:8080 clojure-service
