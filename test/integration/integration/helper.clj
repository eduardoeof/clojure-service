(ns integration.helper
  (:require [io.pedestal.http :as bootstrap]
            [clojure-service.server :as server]))

(defn create-service []
  (::bootstrap/service-fn (bootstrap/create-servlet (server/build-service-map))))
