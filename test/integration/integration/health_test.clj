(ns integration.health-test 
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [clojure-service.component :as component]
            [integration.util.http :refer [http-get edn->json]]))

(def components (atom nil))

(defn fixtures-once [test-case]
  (reset! components (component/create-and-start)) 
  (test-case)
  (reset! components nil))

(use-fixtures :once fixtures-once)

(deftest health-test
  (let [response (http-get "/api/health" @components)]
    (is (match? {:status 200
                 :body (edn->json {:message "I have a dream - Martin Luther King, Jr."})} 
                response))))
