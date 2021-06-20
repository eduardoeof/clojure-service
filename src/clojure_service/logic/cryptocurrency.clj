(ns clojure-service.logic.cryptocurrency
  (:require [java-time :as time]))

(defn create-cryptocurrency [cryptocurrency]
  (assoc cryptocurrency 
         :id (java.util.UUID/randomUUID)
         :created-at (time/zoned-date-time)))

