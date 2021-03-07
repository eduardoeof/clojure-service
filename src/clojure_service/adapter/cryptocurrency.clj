(ns clojure-service.adapter.cryptocurrency
  (:require [clj-time.coerce :as time]
            [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency :as schema]))

(defn request-body->dto [body]
  {:pre  [(s/valid? ::schema/request-body body)]
   :post [(s/valid? ::schema/dto %)]}
  (let [usd-last-updated (-> body :quote :USD :last-updated)
        btc-last-updated (-> body :quote :BTC :last-updated)]
    (-> body
        (assoc-in [:quote :USD :last-updated] (time/from-string usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/from-string btc-last-updated)))))

(defn cryptocurrency->response-body [cryptocurrency]
  {:pre  [(s/valid? ::schema/cryptocurrency cryptocurrency)]
   :post [(s/valid? ::schema/response-body %)]}
  (let [id (:id cryptocurrency)
        created-at (:created-at cryptocurrency)
        usd-last-updated (-> cryptocurrency :quote :USD :last-updated)
        btc-last-updated (-> cryptocurrency :quote :BTC :last-updated)]
    (-> cryptocurrency 
        (assoc :id (str id))
        (assoc :created-at (time/to-string created-at))
        (assoc-in [:quote :USD :last-updated] (time/to-string usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/to-string btc-last-updated)))))
