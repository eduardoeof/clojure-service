(ns clojure-service.controller
  (:require [clj-time.core :as time.core]
            [clj-time.coerce :as time.coerce]))

(defn ^:dynamic create-cryptocurrency [cryptocurrency]
  (let [usd-last-updated (-> cryptocurrency :quote :USD :last-updated) 
        btc-last-updated (-> cryptocurrency :quote :BTC :last-updated)]
    (-> cryptocurrency
        (assoc :id (java.util.UUID/randomUUID))
        (assoc :created-at (time.core/now))
        (assoc-in [:quote :USD :last-updated] usd-last-updated)
        (assoc-in [:quote :BTC :last-updated] btc-last-updated))))

