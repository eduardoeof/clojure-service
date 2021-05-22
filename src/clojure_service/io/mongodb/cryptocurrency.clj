(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.collection :as monger]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def collection "cryptocurrencies")

(defn insert! 
  [cryptocurrency 
   {:keys [db] :as _mongodb}]
  (-> db
      (monger/insert-and-return collection cryptocurrency)
      adapter/mongodb-document->cryptocurrency))

(defn find-maps [{:keys [db] :as _mongodb}]
  (->> collection
       (monger/find-maps db)
       (map adapter/mongodb-document->cryptocurrency)))

