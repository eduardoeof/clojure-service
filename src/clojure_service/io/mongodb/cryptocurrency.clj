(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.core :as mg]
            [io.pedestal.log :as log]
            [monger.collection :as mc]))

(def coll "cryptocurrencies")

(defn insert! [cryptocurrency]
  (let [connection (monger.core/connect {:host "0.0.0.0" 
                                         :port 27017})
        db (monger.core/get-db connection "clojure-service-db")]
    (monger.collection/insert-and-return db coll cryptocurrency)))

