(ns clojure-service.adapter.cryptocurrency-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [java-time :as time]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def last-updated "2018-08-09T22:53:32.000")
(def last-update-date-time (time/local-date-time last-updated))

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

(def cryptocurrency (-> request-body
                        (assoc :id (java.util.UUID/randomUUID)
                               :created-at (time/local-date-time))  
                        (assoc-in [:quote :USD :last-updated] last-update-date-time)
                        (assoc-in [:quote :BTC :last-updated] last-update-date-time)))

(def mongodb-document (assoc cryptocurrency :_id (java.util.UUID/randomUUID)))

(deftest request-body->dto-test
  (testing "should adapt a request body to a dto"
    (is (match? {:quote {:USD {:last-updated last-update-date-time}
                         :BTC {:last-updated last-update-date-time}}}
                (adapter/request-body->dto request-body))))
  
  (testing "should thrown an exception when passed a non request body"
    (let [fake-request-body {:x 1}]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency\/request-body body\)"
                            (adapter/request-body->dto fake-request-body))))))

(deftest cryptocurrency->response-body-test
  (testing "should adapt a cryptocurrency in a response body"
    (is (match? {:id string? 
                 :created-at string? 
                 :quote {:USD {:last-updated last-updated}
                         :BTC {:last-updated last-updated}}}
                (adapter/cryptocurrency->response-body cryptocurrency))))
  
  (testing "should thrown an exception when passed a non response body"
    (let [fake-response-body {:y 1}]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency\/cryptocurrency cryptocurrency\)"
                            (adapter/cryptocurrency->response-body fake-response-body))))))

(deftest mongodb-document->cryptocurrency
  (testing "should adapt a mongodb document to a cryptocurrency"
    (is (match? cryptocurrency
                (adapter/mongodb-document->cryptocurrency mongodb-document)))))
