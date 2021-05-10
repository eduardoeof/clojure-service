(ns integration.cryptocurrencies-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer [response-for]]
            [matcher-combinators.test :refer [match?]]
            [clojure.data.json :as json] 
            [integration.helper :as helper]
            [clojure-service.controller :as controller]))

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

(def response-body (merge {:id string?
                           :created-at string?}
                          request-body))

(defn- http-post [endpoint body]
  (response-for (helper/create-service) 
                :post endpoint 
                :headers {"Content-Type" "application/json"}  
                :body body))

(defn- http-get [endpoint]
  (response-for (helper/create-service) 
                :get endpoint 
                :headers {"Content-Type" "application/json"}))

(deftest post-cryptocurrencies-test
  (testing "should create a cryptocurrency with success"
    (let [response (http-post "/api/cryptocurrencies" (json/write-str request-body))]

      (is (match? {:status 201}
                  response))  
      (is (match? response-body 
                  (json/read-str (:body response)
                                 :key-fn keyword)))))
  
  (testing "should responde bad request error when tried to create a cryptocurrency"
    (let [body (dissoc request-body :name)
          response (http-post "/api/cryptocurrencies" (json/write-str body))]

      (is (match? {:status 400
                   :body (json/write-str {:message "Request not valid"})}
                  response))))
  
  (testing "should responde internal server error when response doesn't match to a cryptocurrency"
    (binding [controller/create-cryptocurrency (fn [_] {})]
      (let [response (http-post "/api/cryptocurrencies" (json/write-str request-body))]

        (is (match? {:status 500
                     :body (json/write-str {:message "Internal server error"})}
                    response))))))

(deftest get-cryptocurrencies-test
  (testing "should get all saved cryptocurrencies"
    (http-post "/api/cryptocurrencies" request-body)

    (let [response (http-get "/api/cryptocurrencies")]
      (is (match? {:status 200}
                  response))

      (is (match? [response-body]
                  (json/read-str (:body response)
                                 :key-fn keyword)))))

  (testing "should response internal server error when response doesn't match to a vector of cryptocurrency"
    (binding [controller/fetch-cryptocurrencies (fn [_] ["fake value"])]
      (let [response (http-get "/api/cryptocurrencies")]

        (is (match? {:status 500
                     :body (json/write-str {:message "Internal server error"})}
                    response))))))

