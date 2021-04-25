(ns clojure-service.logic.cryptocurrency
  (:require [java-time :as time]))

(defn create-cryptocurrency [dto]
  (-> dto 
      (assoc :id (java.util.UUID/randomUUID))
      (assoc :created-at (time/local-date-time))))

