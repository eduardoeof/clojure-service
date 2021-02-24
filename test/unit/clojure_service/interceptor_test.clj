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

(defn- route-handler [] (+ 1 1))

(def system-map {:io.pedestal.http/routes #{["/route" :get `route-handler]}})

(deftest bad-request-interceptor-test
  (testing "should create bad request interceptor"
    (is (match? {:name :clojure-service.interceptor/bad-request-interceptor
                 :enter function?
                 :leave nil}
                (interceptor/bad-request-interceptor ::body))))

  (testing "should not throw an exception when a request matches the schema"
    (let [interceptor (interceptor/bad-request-interceptor ::body)
          func (:enter interceptor)]
      (is (match? context-request
                  (func context-request)))))

  (testing "Interceptor throws an exception when a request doesn't match the schema"
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

  (testing "should not throw an exception when a response matches the schema"
    (let [interceptor (interceptor/bad-response-interceptor ::body)
          func (:leave interceptor)]
      (is (match? context-response
                  (func context-response)))))

  (testing "should throw an exception when a response doesn't match the schema"
    (let [interceptor (interceptor/bad-response-interceptor ::body)
          func (:leave interceptor)
          wrong-context (assoc-in context-response [:response :body :point :x] "wrong value")]
      (is (thrown-match? {:exception-type :clojure-service.interceptor/bad-response-exception}
                         (func wrong-context))))))

(deftest wrap-interceptors-test
  (let [system-map (interceptor/wrap-interceptors system-map)]
    (testing "system-map should have 11 interceptors in system-map"
      (is (match? 11
                  (-> system-map
                      :io.pedestal.http/interceptors
                      count))))

    (testing "system-map should contain json-body interceptor"
      (is (some? (filter #((= :io.pedestal.http/json-body (:name %)))
                         (:io.pedestal.http/interceptors system-map))))))
      
    (testing "system-map should contain body-params interceptor"
      (is (some? (filter #((= :io.pedestal.http/body-params (:name %)))
                         (:io.pedestal.http/interceptors system-map))))) 
    
    (testing "system-map should contain error-handler interceptor"
      (is (some? (filter #((= :clojure-service.interceptor/error-handler-interceptor (:name %)))
                         (:io.pedestal.http/interceptors system-map))))))
