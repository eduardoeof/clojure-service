(ns integration.cryptocurrencies-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [clojure.data.json :as json] 
            [integration.helper :as helper]))

(def request-body 
  (json/write-str {:name "Bitcoin"
                   :type :BTC
                   :slug "bitcoin"
                   :quote {"USD" {:price 9283.92
                                  :percent-change-1h -0.152774
                                  :percent-change-24h 0.518894
                                  :percent-change-7d 0.986573
                                  :last-updated "2018-08-09T22:53:32.000Z"}
                           :BTC {:price 1 
                                 :volume-24h 772012
                                 :percent-change-1h 0 
                                 :percent-change-24h 0
                                 :percent-change-7d 0
                                 :last-updated "2018-08-09T22:53:32.000Z"}}}))

(def response-body 
  (json/write-str {:id "3edf8b2a-6962-11eb-9439-0242ac130002" 
                   :last-updated "2018-06-02T22:51:28.209Z" 
                   :name "Bitcoin"
                   :type :BTC
                   :slug "bitcoin"
                   :quote {"USD" {:price 9283.92
                                  :percent-change-1h -0.152774
                                  :percent-change-24h 0.518894
                                  :percent-change-7d 0.986573
                                  :last-updated "2018-08-09T22:53:32.000Z"}
                           :BTC {:price 1 
                                 :volume-24h 772012
                                 :percent-change-1h 0 
                                 :percent-change-24h 0
                                 :percent-change-7d 0
                                 :last-updated "2018-08-09T22:53:32.000Z"}}}))

(deftest cryptocurrencies-test
  (testing "Create a cryptocurrency"
    (let [response (response-for helper/service 
                                 :post "/api/cryptocurrencies"
                                 :headers {"Content-Type" "application/json"}  
                                 :body request-body)]
      (is (match? {:status 201
                   :body response-body}
                  response)))))

