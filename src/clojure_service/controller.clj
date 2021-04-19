(ns clojure-service.controller
  (:require [clojure-service.logic.cryptocurrency :as logic]
            [clojure-service.io.mongodb.cryptocurrency :as mongodb]))

(defn ^:dynamic create-cryptocurrency [dto]
  (-> dto
      logic/create-cryptocurrency
      mongodb/insert!))

