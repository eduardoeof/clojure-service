(ns integration.health-test 
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [integration.helper :as helper]))

(deftest health-test
  (let [response (response-for helper/service :get "/api/health")]
    (is (match? {:status 200
                 :body "{\"message\":\"I have a dream - Martin Luther King, Jr.\"}"}
                response))))
