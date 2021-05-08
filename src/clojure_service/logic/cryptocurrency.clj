(ns clojure-service.logic.cryptocurrency
  (:require [java-time :as time]))

(defn create-cryptocurrency [dto]
  (assoc dto 
         :id (java.util.UUID/randomUUID)
         :created-at (time/local-date-time)))

