(ns clojure-service.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as http.body-params]
            [clojure-service.service :as service]))

(def service-map {:env :prod
                  ::http/type :jetty
                  ::http/host "0.0.0.0"
                  ::http/port 8080
                  ::http/resource-path "/public" ;; TODO: check if it is necessary
                  ::http/container-options {:h2c? true
                                            :h2? false
                                            :ssl? false}})

(defn- wrap-routes [service-map]
  (assoc service-map ::http/routes service/routes))

(defn- wrap-interceptors [service-map]
  (-> service-map
      http/default-interceptors
      (update ::http/interceptors conj (http.body-params/body-params))
      (update ::http/interceptors conj http/json-body)))

(defn build-service-map []
  (-> service-map
      wrap-routes
      wrap-interceptors))

(defn -main
  [& args]
  (println "\nCreating your server...")
  (-> (build-service-map) 
      http/create-server
      http/start))

