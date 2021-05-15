(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.core :as m]
            [monger.collection :as mc]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def collection "cryptocurrencies")

(defn insert! 
  [cryptocurrency 
   {:keys [db] :as _mongodb}]
  (-> db
      (mc/insert-and-return collection cryptocurrency)
      adapter/mongodb-document->cryptocurrency))

(defn find!  [{:keys [db] :as _mongodb}]
  (->> collection
       (mc/find db)
       (map adapter/mongodb-document->cryptocurrency)))
