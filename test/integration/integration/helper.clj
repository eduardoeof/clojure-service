(ns integration.helper
  (:require [io.pedestal.http :as bootstrap]
            [clojure-service.server :as server]))

(defn create-service []
  (::bootstrap/service-fn (-> server/service-map
                              server/build-service-map
                              bootstrap/create-servlet)))

