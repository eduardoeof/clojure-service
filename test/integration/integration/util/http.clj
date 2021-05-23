(ns integration.util.http
  (:require [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as bootstrap]
            [clojure-service.server :as server]))

(defn- create-service [components]
  (::bootstrap/service-fn (-> server/service-map
                              (server/build-service-map components)
                              bootstrap/create-servlet)))

(defn http-post [endpoint body components]
  (response-for (create-service components) 
                :post endpoint 
                :headers {"Content-Type" "application/json"}  
                :body body))

(defn http-get [endpoint components]
  (response-for (create-service components) 
                :get endpoint 
                :headers {"Content-Type" "application/json"}))

(defn json->edn [json]
  (json/read-str json :key-fn keyword))

(defn edn->json [edn]
  (json/write-str edn))
