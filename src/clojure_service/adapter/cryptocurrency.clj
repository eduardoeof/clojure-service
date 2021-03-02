(ns clojure-service.adapter.cryptocurrency
  (:require [clj-time.coerce :as time]))

(defn request-body->model [body]
  (let [usd-last-updated (-> body :quote :USD :last-updated)
        btc-last-updated (-> body :quote :BTC :last-updated)]
    (-> body
        (assoc-in [:quote :USD :last-updated] (time/from-string usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/from-string btc-last-updated)))))
