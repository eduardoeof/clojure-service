(ns clojure-service.interceptor
  (:require [io.pedestal.http :as http]
            [io.pedestal.log :as log]
            [clojure.spec.alpha :as spec]  
            [io.pedestal.interceptor :as i]
            [io.pedestal.http.body-params :as http.body-params]))

(defn bad-request-interceptor [body-schema]
  (i/interceptor
    {:name ::bad-request-interceptor
     :enter (fn [{:keys [request] :as context}]
              (let [body (:json-params request)]
                (if-not (spec/valid? body-schema body)
                  (do
                    (log/error :message "Body request not valid"
                               :schema body-schema
                               :body body)
                    (throw (ex-info "Body request doesn't match with expected schema"
                                    {:exception-type ::bad-request})))
                  context)))
     :error (fn [context ex]
              (if (= ::bad-request (:exception-type (ex-data ex)))
                (do
                  (log/error :message "Invalid request"
                             :exception ex)
                  (assoc context :response {:body {:error "Request not valid"}
                                            :status 400}))
                context))}))

(defn wrap-interceptors [service-map]
  (-> service-map
      http/default-interceptors
      (update ::http/interceptors conj (http.body-params/body-params))
      (update ::http/interceptors conj http/json-body)))

