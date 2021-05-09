(ns clojure-service.io.mongodb.cryptocurrency
  (:require [monger.core :as m]
            [monger.collection :as mc]
            [clojure-service.adapter.cryptocurrency :as adapter]))

(def collection "cryptocurrencies")

(defn insert! [cryptocurrency config]
  (let [connection (m/connect {:host (:mongodb/host config) 
                               :port (:mongodb/port config)})
        db (m/get-db connection (:mongodb/database config))]
    (-> db
        (mc/insert-and-return collection cryptocurrency)
        adapter/mongodb-document->cryptocurrency)))

