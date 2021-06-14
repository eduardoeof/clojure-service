(ns clojure-service.controller
  (:require [clojure-service.logic.cryptocurrency :as logic]
            [clojure-service.io.mongodb.cryptocurrency :as mongodb]))

(defn ^:dynamic create-cryptocurrency 
  [cryptocurrency
   {:keys [mongodb] :as _components}]
  (-> cryptocurrency 
      logic/create-cryptocurrency
      (mongodb/insert! mongodb)))

(defn ^:dynamic get-cryptocurrencies 
  [{:keys [id type] :as _params} 
   {:keys [mongodb] :as _components}]
  (cond
    id    (-> id 
              (mongodb/find-by-id mongodb)
              vector)
    type  (mongodb/find-all-by-type type mongodb)
    :else (mongodb/find-maps mongodb)))

