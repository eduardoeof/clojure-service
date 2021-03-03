(ns clojure-service.adapter.cryptocurrency
  (:require [clj-time.coerce :as time]))

(defn request-body->model [body]
  (let [usd-last-updated (-> body :quote :USD :last-updated)
        btc-last-updated (-> body :quote :BTC :last-updated)]
    (-> body
        (assoc-in [:quote :USD :last-updated] (time/from-string usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/from-string btc-last-updated)))))

(defn model->response-body [model]
  (let [id (:id model)
        created-at (:created-at model)
        usd-last-updated (-> model :quote :USD :last-updated)
        btc-last-updated (-> model :quote :BTC :last-updated)]
    (-> model 
        (assoc :id (str id))
        (assoc :created-at (time/to-string created-at))
        (assoc-in [:quote :USD :last-updated] (time/to-string usd-last-updated))
        (assoc-in [:quote :BTC :last-updated] (time/to-string btc-last-updated)))))
