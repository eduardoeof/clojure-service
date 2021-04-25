(ns clojure-service.schema.cryptocurrency.model
  (:require [clojure.spec.alpha :as s]
            [java-time :refer [local-date-time?]]))

(s/def ::id uuid?)
(s/def ::created-at local-date-time?)
(s/def ::price float?)
(s/def ::percent-change-1h float?)
(s/def ::percent-change-24h float?)
(s/def ::percent-change-7d float?)
(s/def ::last-updated local-date-time?)
(s/def ::volume-24h int?)

(s/def ::USD (s/keys :req-un [::price
                              ::percent-change-1h
                              ::percent-change-24h
                              ::percent-change-7d
                              ::last-updated])) 

(s/def ::BTC (s/keys :req-un [::price
                              ::percent-change-1h
                              ::percent-change-24h
                              ::percent-change-7d
                              ::last-updated
                              ::volume-24h]))

(s/def ::quote (s/keys :req-un [::USD ::BTC]))
