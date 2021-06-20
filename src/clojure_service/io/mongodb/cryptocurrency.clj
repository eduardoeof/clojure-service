(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.collection :as monger]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def collection "cryptocurrencies")

(defn insert! 
  [cryptocurrency 
   {:keys [db] :as _mongodb}]
  (->> cryptocurrency 
       adapter/cryptocurrency->mongodb-document
       (monger/insert-and-return db collection)
       adapter/mongodb-document->cryptocurrency))

(defn find-all [{:keys [db] :as _mongodb}]
  (->> collection
       (monger/find-maps db)
       (map adapter/mongodb-document->cryptocurrency)))

(defn find-by-id 
  [id
   {:keys [db] :as _mongodb}]
  (->> {:id id}
       (monger/find-one-as-map db collection)
       adapter/mongodb-document->cryptocurrency))

(defn find-all-by-type
  [type
   {:keys [db] :as _mongodb}]
  (->> {:type type}
       (monger/find-maps db collection)
       (map adapter/mongodb-document->cryptocurrency)))


