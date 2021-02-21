(ns clojure-service.interceptor-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [matcher-combinators.test :refer [match? thrown-match?]]
            [clojure-service.interceptor :as interceptor]))

(s/def ::x int?)
(s/def ::y int?)
(s/def ::point (s/keys :req-un [::x ::y]))
(s/def ::body (s/keys :req-un [::point]))

(def context {:request {:json-params {:point {:x 1
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
      (is (match? context
                  (func context)))))

  (testing "Interceptor throws an exception when input doesn't matches the schema"
    (let [interceptor (interceptor/bad-request-interceptor ::body)
          func (:enter interceptor)
          wrong-context (assoc-in context [:request :json-params :point :x] "wrong value")]
      (is (thrown-match? {:exception-type :clojure-service.interceptor/bad-request-exception}
                         (func wrong-context))))))

