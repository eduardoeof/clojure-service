(ns clojure-service.adapter.cryptocurrency-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test :refer [match?]]
            [java-time :as time]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def id (java.util.UUID/randomUUID))
(def created-at-instant (time/instant))
(def created-at (time/local-date-time created-at-instant (time/zone-id "UTC")))
(def last-updated "2018-08-09T22:53:32.000")
(def last-update-date-time (time/local-date-time last-updated))
(def last-update-instant #inst "2018-08-09T22:53:32.000-00:00")

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

(def cryptocurrency-json (assoc request-body 
                                :id (str id) 
                                :created-at (time/format created-at)))

(def cryptocurrency (-> request-body 
                        (assoc :id         id 
                               :created-at created-at)
                        (assoc-in [:quote :USD :last-updated] last-update-date-time)
                        (assoc-in [:quote :BTC :last-updated] last-update-date-time)))

(def mongodb-document (-> cryptocurrency
                          (assoc :_id (java.util.UUID/randomUUID)
                                 :created-at created-at-instant)
                          (assoc-in [:quote :USD :last-updated] last-update-instant)
                          (assoc-in [:quote :BTC :last-updated] last-update-instant)))

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

(deftest cryptocurrencies->response-body-test
  (testing "should adapt an vector of cryptocurrency in a response body"
    (is (match? {:cryptocurrencies [cryptocurrency-json]}
                (adapter/cryptocurrencies->response-body [cryptocurrency]))))  

  (testing "should thrown an exception when passed a vector of non cryptocurrency"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert failed: \(s\/valid\? :clojure-service.schema.cryptocurrency.model\/cryptocurrencies cryptocurrencies\)"
                          (adapter/cryptocurrencies->response-body ["fake-crypto"])))))

(deftest query-and-path-params->params-test
  (testing "should adapt a query-params and a path-params to internal params"
    (let [query-params {:type "BTC"}
          path-params  {:id (str id)}]
      (is (match? {:id id
                   :type "BTC"}
                  (adapter/query-and-path-params->params query-params path-params)))))

  (testing "should adapt a nil query-params and a path-params to internal params"
    (let [query-params nil 
          path-params  {:id (str id)}]
      (is (match? {:id id}
                  (adapter/query-and-path-params->params query-params path-params)))))

  (testing "should adapt a query-params and a nil path-params to internal params"
    (let [query-params {:type "BTC"} 
          path-params  {}]
      (is (match? {:type "BTC"}
                  (adapter/query-and-path-params->params query-params path-params)))))

  (testing "should create a empty map when query-params and path-params are nil"
    (is (empty? (adapter/query-and-path-params->params nil {})))))

(comment
  (clojure.spec.alpha/check-asserts true)
  (clojure.spec.alpha/assert :clojure-service.schema.cryptocurrency.model/cryptocurrencies [cryptocurrency]))

