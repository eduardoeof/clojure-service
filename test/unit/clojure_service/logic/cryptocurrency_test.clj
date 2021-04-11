(ns clojure-service.logic.cryptocurrency-test
  (:require [clojure.test :refer :all]
            [clojure-service.logic.cryptocurrency :as logic]
            [matcher-combinators.test :refer [match?]]
            [clj-time.core :as time.core]
            [clj-time.types :as time.types]))

(def dto {:name "Bitcoin"
          :type "BTC"
          :slug "bitcoin"
          :quote {:USD {:price 9283.92
                        :percent-change-1h -0.152774
                        :percent-change-24h 0.518894
                        :percent-change-7d 0.986573
                        :last-updated (time.core/now)}
                  :BTC {:price 1.0 
                        :volume-24h 772012
                        :percent-change-1h 0.0 
                        :percent-change-24h 0.0
                        :percent-change-7d 0.0
                        :last-updated (time.core/now)}}})

(def cryptocurrency (merge dto 
                           {:id uuid?
                            :created-at time.types/date-time?}))

(deftest create-cryptocurrency-test
  (testing "it should create a cryptocurrency from a dto"
    (is (match? cryptocurrency 
                (logic/create-cryptocurrency dto)))))

