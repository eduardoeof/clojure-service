(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.core :as mg]
            [io.pedestal.log :as log]
            [monger.collection :as mc]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def coll "cryptocurrencies")

(defn insert! [cryptocurrency]
  (let [connection (monger.core/connect {:host "0.0.0.0" 
                                         :port 27017})
        db (monger.core/get-db connection "clojure-service-db")]
    (-> db
        (monger.collection/insert-and-return coll cryptocurrency)
        adapter/mongodb-document->cryptocurrency)))

