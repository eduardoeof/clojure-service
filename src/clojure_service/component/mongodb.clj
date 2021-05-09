(ns clojure-service.component.mongodb
  (:require [com.stuartsierra.component :as component]
            [monger.core :as mg]))

(defrecord MongoDB []
  component/Lifecycle
  (start [{:keys [config] :as this}]
    (let [connection (mg/connect {:host (:mongodb/host config)
                                  :port (:mongodb/port config)})
          db (mg/get-db connection (:mongodb/database config))]
      (assoc this 
             :connection connection
             :db db)))

  (stop [this]
    (mg/disconnect (:connection this))
    (assoc this 
           :connection nil
           :db nil)))

(defn new-mongo-db []
  (map->MongoDB {}))

