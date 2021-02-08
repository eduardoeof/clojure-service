(ns clojure-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]))

(def common-interceptors [(body-params/body-params) 
                          http/json-body])

(defn- health-check 
  [_request]
  {:status 200
   :body {:message "I have a dream - Martin Luther King, Jr."}})

(def routes #{["/api/health" :get `health-check]})
