(ns clojure-service.service
  (:require [clojure-service.interceptor :as interceptor]
            [clojure-service.schema.cryptocurrency :as schema.cryptocurrency]
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


(defn- fetch-cryptocurrencies
  [{:keys [components] :as _request}]
  (let [cryptocurrencies (controller/fetch-cryptocurrencies components)
        body (map adapter/cryptocurrency->response-body cryptocurrencies)]
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
     :post [(interceptor/bad-request-interceptor  ::schema.cryptocurrency/request-body)
            (interceptor/bad-response-interceptor ::schema.cryptocurrency/response-body)
            `create-cryptocurrency]]

    ["/api/cryptocurrencies"
     :get [`fetch-cryptocurrencies]]})

