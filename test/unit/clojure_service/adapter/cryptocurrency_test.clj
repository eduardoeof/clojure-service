(ns clojure-service.adapter.cryptucurrency-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [clj-time.coerce :as time]
            [clojure-service.adapter.cryptucurrency :as adapter]))

(def last-updated "2018-08-09T22:53:32.000Z")

(def request-body {:name "Bitcoin"
                   :type "BTC"
                   :slug "bitcoin"
                   :quote {:USD {:price 9283.92
                                  :percent-change-1h -0.152774
                                  :percent-change-24h 0.518894
                                  :percent-change-7d 0.986573
                                  :last-updated last-updated}
                           :BTC {:price 1.0 
                                 :volume-24h 772012
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated last-updated}}})

(deftest request-body->model-test
  (is (match? {:quote {:USD {:last-updated (time/from-string last-updated)}
                       :BTC {:last-updated (time/from-string last-updated)}}}
              (adapter/request-body->model request-body))))
