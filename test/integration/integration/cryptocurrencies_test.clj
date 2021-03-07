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
                                  :last-updated "2018-08-09T22:53:32.000Z"}
                           :BTC {:price 1.0 
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated "2018-08-09T22:53:32.000Z"
                                 :volume-24h 772012}}})

(def response-body (merge {:id string?
                           :created-at string?}
                          request-body))

(deftest cryptocurrencies-test
  (testing "Create a cryptocurrency with success"
    (let [response (response-for (helper/create-service) 
                                 :post "/api/cryptocurrencies"
                                 :headers {"Content-Type" "application/json"}  
                                 :body (json/write-str request-body))]
      (is (match? 201
                  (:status response)))  
      (is (match? response-body 
                  (json/read-str (:body response)
                                 :key-fn keyword)))))
  
  (testing "Bad request error when tried to create a cryptocurrency"
    (let [body (dissoc request-body :name)
          response (response-for (helper/create-service) 
                                 :post "/api/cryptocurrencies"
                                 :headers {"Content-Type" "application/json"}  
                                 :body (json/write-str body))]
      (is (match? {:status 400
                   :body (json/write-str {:message "Request not valid"})}
                  response))))
  
  (testing "it should respond internal server error because response doesn't match the expected schema"
    (binding [controller/create-cryptocurrency (fn [_] {})]
      (let [response (response-for (helper/create-service) 
                                   :post "/api/cryptocurrencies"
                                   :headers {"Content-Type" "application/json"}  
                                   :body (json/write-str request-body))]

        (is (match? {:status 500
                     :body (json/write-str {:message "Internal server error"})}
                    response))))))

