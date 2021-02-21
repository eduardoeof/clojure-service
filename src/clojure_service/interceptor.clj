(ns clojure-service.interceptor
  (:require [io.pedestal.http :as http]
            [io.pedestal.log :as log]
            [clojure.spec.alpha :as spec]  
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.error :as interceptor.error]
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
                                    {:exception-type ::bad-request-exception})))
                  context)))}))

(defn bad-response-interceptor [body-schema]
  (i/interceptor
    {:name ::bad-response-interceptor
     :leave (fn [{:keys [response] :as context}]
              (let [body (:body response)]
                (if-not (spec/valid? body-schema body)
                  (do
                    (log/error :message "Body response not valid"
                               :schema body-schema
                               :body body)
                    (throw (ex-info "Body response doesn't match with expected schema"
                                    {:exception-type ::bad-response-exception})))
                  context)))}))

(def ^:private error-handler-interceptor
  (interceptor.error/error-dispatch [context ex]
    [{:exception-type ::bad-request-exception}]
    (assoc context :response {:body {:error "Request not valid"}
                              :status 400})

    [{:exception-type ::bad-response-exception}]
    (assoc context :response {:body {:error "Response not valid"}
                              :status 500})
    :else
    (assoc context :io.pedestal.interceptor.chain/error ex)))
  

(defn wrap-interceptors [service-map]
  (-> service-map
      http/default-interceptors
      (update ::http/interceptors conj (http.body-params/body-params))
      (update ::http/interceptors conj http/json-body)
      (update ::http/interceptors conj error-handler-interceptor)))

