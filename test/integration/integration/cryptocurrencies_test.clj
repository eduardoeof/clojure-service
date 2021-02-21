(ns integration.cryptocurrencies-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [clojure.data.json :as json] 
            [integration.helper :as helper]))

(def request-body {:name "Bitcoin"
                   :type "BTC"
                   :slug "bitcoin"
                   :quote {:USD {:price 9283.92
                                  :percent-change-1h -0.152774
                                  :percent-change-24h 0.518894
                                  :percent-change-7d 0.986573
                                  :last-updated "2018-08-09T22:53:32.000Z"}
                           :BTC {:price 1.0 
                                 :volume-24h 772012
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated "2018-08-09T22:53:32.000Z"}}})

(def response-body {:id "3edf8b2a-6962-11eb-9439-0242ac130002" 
                    :created-at "2018-06-02T22:51:28.209Z" 
                    :name "Bitcoin"
                    :type "BTC"
                    :slug "bitcoin"
                    :quote {:USD {:price 9283.92
                                   :percent-change-1h -0.152774
                                   :percent-change-24h 0.518894
                                   :percent-change-7d 0.986573
                                   :last-updated "2018-08-09T22:53:32.000Z"}
                            :BTC {:price 1.0 
                                  :volume-24h 772012
                                  :percent-change-1h 0.0 
                                  :percent-change-24h 0.0
                                  :percent-change-7d 0.0
                                  :last-updated "2018-08-09T22:53:32.000Z"}}})

(deftest cryptocurrencies-test
  (testing "Create a cryptocurrency with success"
    (let [response (response-for (helper/create-service) 
                                 :post "/api/cryptocurrencies"
                                 :headers {"Content-Type" "application/json"}  
                                 :body (json/write-str request-body))]
      (is (match? {:status 201
                   :body (json/write-str response-body)}
                  response))))
  
  (testing "Bad request error when tried to create a cryptocurrency"
    (let [body (dissoc request-body :name)
          response (response-for (helper/create-service) 
                                 :post "/api/cryptocurrencies"
                                 :headers {"Content-Type" "application/json"}  
                                 :body (json/write-str body))]
      (is (match? {:status 400
                   :body (json/write-str {:error "Request not valid"})}
                  response)))))

