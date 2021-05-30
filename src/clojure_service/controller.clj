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
  [{:keys [mongodb] :as _components}]
  (mongodb/find-maps mongodb))

