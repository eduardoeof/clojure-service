(ns clojure-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(defn- create-cryptocurrency 
  [{:keys [json-params] :as _request}]
  {:status 201
   :body (merge {:id "3edf8b2a-6962-11eb-9439-0242ac130002"
                 :last-updated "2018-06-02T22:51:28.209Z"}
                json-params)})

(defn- health-check 
  [_request]
  {:status 200
   :body {:message "I have a dream - Martin Luther King, Jr."}})

(def routes #{["/api/health"           :get  `health-check]
              ["/api/cryptocurrencies" :post `create-cryptocurrency]})
