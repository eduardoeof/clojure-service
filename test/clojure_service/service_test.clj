(ns clojure-service.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [matcher-combinators.test :refer [match?]]
            [clojure-service.server :as server]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet (server/build-service-map))))

(deftest health-test
  (let [response (response-for service :get "/api/health")]
    (is (match? {:status 200
                 :body "{\"message\":\"I have a dream - Martin Luther King, Jr.\"}"}
                response))))
