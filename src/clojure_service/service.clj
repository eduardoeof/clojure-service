(ns clojure-service.service
  (:require [clojure-service.interceptor :as interceptor]
            [clojure-service.schema.cryptocurrency :as schema.cryptocurrency]
            [clojure-service.controller :as controller]))

(defn- create-cryptocurrency 
  [{:keys [json-params] :as _request}]
  {:status 201
   :body (controller/create-cryptocurrency json-params)})

(defn- health-check 
  [_request]
  {:status 200
   :body {:message "I have a dream - Martin Luther King, Jr."}})

(def routes #{["/api/health"           :get  `health-check]
              ["/api/cryptocurrencies" :post [(interceptor/bad-request-interceptor ::schema.cryptocurrency/request)
                                              (interceptor/bad-response-interceptor ::schema.cryptocurrency/response)
                                              `create-cryptocurrency]]})
