(ns clojure-service.service
  (:require [clojure-service.interceptor :as interceptor]
            [clojure-service.schema.cryptocurrency :as schema.cryptocurrency]
            [clojure-service.controller :as controller]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(defn- create-cryptocurrency 
  [{:keys [json-params] :as _request}]
  (let [body (-> json-params
                 adapter/request-body->model
                 controller/create-cryptocurrency
                 adapter/model->response-body)]
    {:status 201
     :body body}))

(defn- health-check 
  [_request]
  {:status 200
   :body {:message "I have a dream - Martin Luther King, Jr."}})

(def routes #{["/api/health"           :get  `health-check]
              ["/api/cryptocurrencies" :post [(interceptor/bad-request-interceptor ::schema.cryptocurrency/request)
                                              (interceptor/bad-response-interceptor ::schema.cryptocurrency/response)
                                              `create-cryptocurrency]]})
