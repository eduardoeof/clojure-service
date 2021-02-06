(ns clojure-service.server
  (:gen-class) ; for -main method in uberjar
  (:require [io.pedestal.http :as server]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure-service.service :as service]))

(def ^:private service-map {:env :prod
                            ::http/type :jetty
                            ::http/host "0.0.0.0"
                            ::http/port 8080
                            ::http/resource-path "/public" ;; TODO: check if it is necessary
                            ::http/container-options {:h2c? true
                                                      :h2? false
                                                      :ssl? false}})

(defn- wrap-routes [service-map]
  (assoc service-map ::http/routes service/routes))

(defn -main
  [& args]
  (println "\nCreating your server...")
  (-> service-map 
      wrap-routes ; routes need to be first of interceptors, for some reason...
      server/default-interceptors
      http/create-server
      http/start))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (-> service/service 
      (merge {:env :dev
              ::server/join? false
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}
              ::server/secure-headers {:content-security-policy-settings {:object-src "'none'"}}})
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

