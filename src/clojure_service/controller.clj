(ns clojure-service.controller)

(defn ^:dynamic create-cryptocurrency [dto]
  (merge {:id "3edf8b2a-6962-11eb-9439-0242ac130002"
          :created-at "2018-06-02T22:51:28.209Z"}
         dto))
