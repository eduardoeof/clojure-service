(ns clojure-service.logic.cryptocurrency
  (:require [clj-time.core :as time.core]))

(defn create-cryptocurrency [dto]
  (-> dto 
      (assoc :id (java.util.UUID/randomUUID))
      (assoc :created-at (time.core/now))))

