(ns integration.helper
  (:require [io.pedestal.http :as bootstrap]
            [clojure-service.server :as server]))

;; TODO: Move to util/service
(defn create-service [components]
  (::bootstrap/service-fn (-> server/service-map
                              (server/build-service-map components)
                              bootstrap/create-servlet)))

