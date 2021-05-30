(ns clojure-service.service
  (:require [clojure-service.interceptor :as interceptor]
            [clojure-service.schema.cryptocurrency.dto :as schema]
            [clojure-service.controller :as controller]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(defn- create-cryptocurrency 
  [{:keys [json-params components] :as _request}]
  (let [body (-> json-params
                 adapter/request-body->dto
                 (controller/create-cryptocurrency components)
                 adapter/cryptocurrency->response-body)]
    {:status 201
     :body body}))

(defn- get-cryptocurrencies
  [{:keys [components] :as _request}]
  (let [body (-> components
                 controller/get-cryptocurrencies
                 adapter/cryptocurrencies->response-body)]
    {:status 200
     :body body}))

(defn- health-check 
  [_request]
  {:status 200
   :body {:message "I have a dream - Martin Luther King, Jr."}})

(def routes 
  #{["/api/health"           
     :get `health-check]

    ["/api/cryptocurrencies" 
     :post [(interceptor/bad-request-interceptor  ::schema/request-body)
            (interceptor/bad-response-interceptor ::schema/post-response-body)
            `create-cryptocurrency]]

    ["/api/cryptocurrencies"
     :get [(interceptor/bad-response-interceptor ::schema/get-response-body)
           `get-cryptocurrencies]]})


