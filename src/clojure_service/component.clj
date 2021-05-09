(ns clojure-service.component 
  (:require [com.stuartsierra.component :as component]
            [eduardoeof.config-component :as config]))

(defn- create-components []
  (component/system-map
    :config (config/new-config "resources/config.json")))

(defn create-and-start []
  (component/start (create-components)))

