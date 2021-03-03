(ns clojure-service.controller
  (:require [clj-time.core :as time.core]
            [clj-time.coerce :as time.coerce]))

(defn ^:dynamic create-cryptocurrency [dto]
  (let [usd-last-updated (-> dto :quote :USD :last-updated) 
        btc-last-updated (-> dto :quote :BTC :last-updated)]
    (-> dto
        (assoc :id (java.util.UUID/randomUUID))
        (assoc :created-at (time.core/now))
        (assoc-in [:quote :USD :last-updated] usd-last-updated)
        (assoc-in [:quote :BTC :last-updated] btc-last-updated))))
