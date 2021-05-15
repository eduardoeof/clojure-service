(ns clojure-service.controller
  (:require [clojure-service.logic.cryptocurrency :as logic]
            [clojure-service.io.mongodb.cryptocurrency :as mongodb]))

(defn ^:dynamic create-cryptocurrency 
  [dto
   {:keys [mongodb] :as _components}]
  (-> dto
      logic/create-cryptocurrency
      (mongodb/insert! mongodb)))

(defn ^:dynamic fetch-cryptocurrencies [{:keys [mongodb] :as _components}]
  (mongodb/find! mongodb))
