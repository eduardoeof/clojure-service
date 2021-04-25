(ns clojure-service.adapter.cryptocurrency
  (:require [java-time :as time]
            [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency :as schema]))

(def date-time-format "yyyy-MM-dd'T'HH:mm:ss.SSS")

(defn request-body->dto [body]
  {:pre  [(s/valid? ::schema/request-body body)]
   :post [(s/valid? ::schema/dto %)]}
  (let [usd-last-updated (-> body :quote :USD :last-updated)
        btc-last-updated (-> body :quote :BTC :last-updated)]
    (-> body
        (assoc-in [:quote :USD :last-updated] (time/local-date-time usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/local-date-time btc-last-updated)))))

(defn cryptocurrency->response-body [cryptocurrency]
  {:pre  [(s/valid? ::schema/cryptocurrency cryptocurrency)]
   :post [(s/valid? ::schema/response-body %)]}
  (let [id (:id cryptocurrency)
        created-at (:created-at cryptocurrency)
        usd-last-updated (-> cryptocurrency :quote :USD :last-updated)
        btc-last-updated (-> cryptocurrency :quote :BTC :last-updated)]
    (-> cryptocurrency 
        (assoc :id (str id))
        (assoc :created-at (time/format created-at))
        (assoc-in [:quote :USD :last-updated] (time/format date-time-format usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/format date-time-format btc-last-updated)))))

(defn mongodb-document->cryptocurrency [document]
  {:post [(s/valid? ::schema/cryptocurrency %)]}
  (dissoc document :_id))
