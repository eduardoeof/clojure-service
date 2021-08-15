(ns clojure-service.adapter.cryptocurrency-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [java-time :as time]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def id (java.util.UUID/randomUUID))
(def date-time-format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

(def created-at-instant (time/instant))
(def created-at (time/zoned-date-time created-at-instant 
                                      (time/zone-id "Z")))

(def last-updated-instant (time/instant))
(def last-updated (time/zoned-date-time last-updated-instant 
                                        (time/zone-id "Z")))
(def last-updated-str (time/format date-time-format 
                                   last-updated)) 

(def request-body {:name "Bitcoin"
                   :type "BTC"
                   :slug "bitcoin"
                   :quote {:USD {:price 9283.92
                                  :percent-change-1h -0.152774
                                  :percent-change-24h 0.518894
                                  :percent-change-7d 0.986573
                                  :last-updated last-updated-str}
                           :BTC {:price 1.0 
                                 :volume-24h 772012
                                 :percent-change-1h 0.0 
                                 :percent-change-24h 0.0
                                 :percent-change-7d 0.0
                                 :last-updated last-updated-str}}})

(def cryptocurrency-json (assoc request-body 
                                :id         (str id) 
                                :created-at (time/format date-time-format
                                                         created-at)))

(def cryptocurrency (-> request-body 
                        (assoc :id         id 
                               :created-at created-at)
                        (assoc-in [:quote :USD :last-updated] last-updated)
                        (assoc-in [:quote :BTC :last-updated] last-updated)))

(def mongodb-document (-> cryptocurrency
                          (assoc :_id (java.util.UUID/randomUUID)
                                 :created-at created-at-instant)
                          (assoc-in [:quote :USD :last-updated] last-updated-instant)
                          (assoc-in [:quote :BTC :last-updated] last-updated-instant)))

(def mongodb-document-fake (dissoc mongodb-document :name))

(deftest request-body->cryptocurrency-test
  (testing "should adapt a request body to a dto"
    (is (match? (adapter/request-body->cryptocurrency request-body)
                cryptocurrency)))
  
  (testing "should thrown an exception when passed a non request body"
    (let [fake-request-body {:x 1}]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.dto\/request-body body\)"
                            (adapter/request-body->cryptocurrency fake-request-body))))))

(deftest cryptocurrency->response-body-test
  (testing "should adapt a cryptocurrency in a response body"
    (is (match? {:cryptocurrency cryptocurrency-json} 
                (adapter/cryptocurrency->response-body cryptocurrency))))
  
  (testing "should thrown an exception when passed a non response body"
    (let [fake-response-body {:y 1}]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.model\/cryptocurrency cryptocurrency\)"
                            (adapter/cryptocurrency->response-body fake-response-body))))))

(deftest mongodb-document->cryptocurrency
  (testing "should adapt a mongodb document to a cryptocurrency"
    (is (match? cryptocurrency
                (adapter/mongodb-document->cryptocurrency mongodb-document)))) 
  
  (testing "should thrown an exception when returned a non cryptocurrency"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.model\/cryptocurrency \%\)"
                          (adapter/mongodb-document->cryptocurrency mongodb-document-fake)))))

(deftest cryptocurrency->mongodb-document-test
  (testing "should adapt a cryptocurrency to a mongodb document"
    (let [mongodb-document (dissoc mongodb-document :_id)]
      (is (match? mongodb-document 
                  (adapter/cryptocurrency->mongodb-document cryptocurrency))))) 
  
  (testing "should thrown an exception when returned a non cryptocurrency"
    (let [fake-cryptocurrency {:x 1}]
      (is (thrown-with-msg? java.lang.AssertionError
                            #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.model\/cryptocurrency cryptocurrency\)"
                            (adapter/cryptocurrency->mongodb-document fake-cryptocurrency))))))

(deftest cryptocurrencies->response-body-test
  (testing "should adapt an vector of cryptocurrency in a response body"
    (is (match? {:cryptocurrencies [cryptocurrency-json]}
                (adapter/cryptocurrencies->response-body [cryptocurrency]))))  

  (testing "should thrown an exception when passed a vector of non cryptocurrency"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.model\/cryptocurrencies cryptocurrencies\)"
                          (adapter/cryptocurrencies->response-body ["fake-crypto"])))))

(deftest query-and-path-params->params-test
  (let [from-date    "2021-07-30T01:13:00.000Z"
        to-date      "2021-07-30T05:13:00.000Z"
        query-params {:type "BTC"
                      :from from-date 
                      :to   to-date}
        path-params  {:id (str id)}]

    (testing "should adapt a query-params and a path-params to internal params"
      (is (match? {:id   id
                   :type "BTC"
                   :from (time/zoned-date-time from-date)  
                   :to   (time/zoned-date-time to-date)}
                  (adapter/query-and-path-params->params query-params path-params))))

    (testing "should adapt a nil query-params and a path-params to internal params"
      (is (match? {:id id}
                  (adapter/query-and-path-params->params nil path-params))))

    (testing "should adapt a query-params and a nil path-params to internal params"
      (is (match? {:type "BTC"
                   :from (time/zoned-date-time from-date)  
                   :to   (time/zoned-date-time to-date)}
                  (adapter/query-and-path-params->params query-params nil))))

    (testing "should create a empty map when query-params and path-params are nil"
      (is (empty? (adapter/query-and-path-params->params nil nil))))))

(comment
  (clojure.spec.alpha/check-asserts true)
  (clojure.spec.alpha/assert :clojure-service.schema.cryptocurrency.model/cryptocurrencies [cryptocurrency]))

