(ns integration.cryptocurrencies-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json] 
            [io.pedestal.test :refer [response-for]]
            [matcher-combinators.test :refer [match?]]
            [integration.util.service :as util.service]
            [integration.util.mongodb :as util.mongodb]
            [clojure-service.controller :as controller]
            [clojure-service.component :as component]))

(def components (atom nil))

(def request-body {:name "Bitcoin"
                   :type "BTC"
                   :slug "bitcoin"
                   :quote {:USD {:price 9283.92
                                 :percent-change-1h -0.152774
                                 :percent-change-24h 0.518894
                                 :percent-change-7d 0.986573
                                 :last-updated "2018-08-09T22:53:32.000"}
                           :BTC {:price 1.0 
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated "2018-08-09T22:53:32.000"
                                 :volume-24h 772012}}})

(def cryptocurrency-json (assoc request-body
                                :id string?
                                :created-at string?))

;; TODO: should be {:cryptocurrency {}}
(def response-body (assoc request-body
                          :id string?
                          :created-at string?))

(def get-response-body {:cryptocurrencies [cryptocurrency-json]})

(defn- http-post [endpoint body]
  (response-for (util.service/create-service @components) 
                :post endpoint 
                :headers {"Content-Type" "application/json"}  
                :body body))

(defn- http-get [endpoint]
  (response-for (util.service/create-service @components) 
                :get endpoint 
                :headers {"Content-Type" "application/json"}))

(defn json->edn [json]
  (json/read-str json :key-fn keyword))

(defn edn->json [edn]
  (json/write-str edn))

(defn fixtures-once [test-case]
  (reset! components (component/create-and-start)) 
  (test-case)
  (reset! components nil))

(defn fixtures-each [test-case]
  (test-case)
  (util.mongodb/drop-collection "cryptocurrencies" (:mongodb @components)))

;; TODO: Try to put all fixtures in teh same call
(use-fixtures :once fixtures-once)
(use-fixtures :each fixtures-each)

(deftest post-cryptocurrencies-test
  (testing "should create a cryptocurrency with success"
    (let [response (http-post "/api/cryptocurrencies" (edn->json request-body))]
      (is (match? {:status 201}
                  response))  
      (is (match? response-body 
                  (json->edn (:body response))))))
  
  (testing "should responde bad request error when tried to create a cryptocurrency"
    (let [body (dissoc request-body :name)
          response (http-post "/api/cryptocurrencies" (edn->json body))]

      (is (match? {:status 400
                   :body (edn->json {:message "Request not valid"})}
                  response))))
  
  (testing "should responde internal server error when response doesn't match to a cryptocurrency"
    (binding [controller/create-cryptocurrency (fn [_] {})]
      (let [response (http-post "/api/cryptocurrencies" (edn->json request-body))]

        (is (match? {:status 500
                     :body (edn->json {:message "Internal server error"})}
                    response))))))

(deftest get-cryptocurrencies-test
  (testing "should get all saved cryptocurrencies"
    (http-post "/api/cryptocurrencies" (edn->json request-body))

    (let [response (http-get "/api/cryptocurrencies")]
      (is (match? {:status 200}
                  response))

      (is (match? {:cryptocurrencies [cryptocurrency-json]}
                  (json->edn (:body response))))))

  (testing "should response internal server error when response doesn't match to a vector of cryptocurrency"
    (binding [controller/get-cryptocurrencies (fn [_] ["fake value"])]
      (let [response (http-get "/api/cryptocurrencies")]

        (is (match? {:status 500
                     :body (edn->json {:message "Internal server error"})}
                    response))))))

