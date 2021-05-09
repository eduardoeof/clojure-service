(ns clojure-service.controller
  (:require [clojure-service.logic.cryptocurrency :as logic]
            [clojure-service.io.mongodb.cryptocurrency :as mongodb]))

(defn ^:dynamic create-cryptocurrency 
  [dto
   {:keys [config] :as _components}]
  (-> dto
      logic/create-cryptocurrency
      (mongodb/insert! config)))

