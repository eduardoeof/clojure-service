(ns clojure-service.component 
  (:require [com.stuartsierra.component :as component]
            [clojure-service.component.mongodb :as mongodb]
            [eduardoeof.config-component :as config]))

(defn- create-components []
  (component/system-map
    :config (config/new-config "resources/config.json")
    :mongodb (component/using (mongodb/new-mongo-db) [:config])))

(defn create-and-start []
  (component/start (create-components)))

