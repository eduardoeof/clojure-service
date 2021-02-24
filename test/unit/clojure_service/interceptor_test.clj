(ns clojure-service.interceptor-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [matcher-combinators.test :refer [match? thrown-match?]]
            [clojure-service.interceptor :as interceptor]))

(s/def ::x int?)
(s/def ::y int?)
(s/def ::point (s/keys :req-un [::x ::y]))
(s/def ::body (s/keys :req-un [::point]))

(def context-request {:request {:json-params {:point {:x 1
                                                      :y 1}}}})

(def context-response {:response {:body {:point {:x 1
                                                 :y 1}}}})

(deftest bad-request-interceptor-test
  (testing "Create bad request interceptor"
    (is (match? {:name :clojure-service.interceptor/bad-request-interceptor
                 :enter function?
                 :leave nil}
                (interceptor/bad-request-interceptor ::body))))

  (testing "Interceptor doesn't thrown an exception when input matches the schema"
    (let [interceptor (interceptor/bad-request-interceptor ::body)
          func (:enter interceptor)]
      (is (match? context-request
                  (func context-request)))))

  (testing "Interceptor throws an exception when input doesn't match the schema"
    (let [interceptor (interceptor/bad-request-interceptor ::body)
          func (:enter interceptor)
          wrong-context (assoc-in context-request [:request :json-params :point :x] "wrong value")]
      (is (thrown-match? {:exception-type :clojure-service.interceptor/bad-request-exception}
                         (func wrong-context))))))

(deftest bad-response-interceptor-test
  (testing "should create a bad response interceptor"
    (is (match? {:name :clojure-service.interceptor/bad-response-interceptor
                 :enter nil 
                 :leave function?}
                (interceptor/bad-response-interceptor ::body))))

  (testing "should not throw an exception when input matches the schema"
    (let [interceptor (interceptor/bad-response-interceptor ::body)
          func (:leave interceptor)]
      (is (match? context-response
                  (func context-response)))))

  (testing "should throw an exception when input doesn't match the schema"
    (let [interceptor (interceptor/bad-response-interceptor ::body)
          func (:leave interceptor)
          wrong-context (assoc-in context-response [:response :body :point :x] "wrong value")]
      (is (thrown-match? {:exception-type :clojure-service.interceptor/bad-response-exception}
                         (func wrong-context))))))
