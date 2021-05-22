(ns clojure-service.schema.cryptocurrency
  (:require [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency.model :as model]))

(s/def ::id string?)
(s/def ::created-at string?)
(s/def ::name string?)
(s/def ::type string?)
(s/def ::slug string?)
(s/def ::price float?)
(s/def ::percent-change-1h float?)
(s/def ::percent-change-24h float?)
(s/def ::percent-change-7d float?)
(s/def ::last-updated string?)
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

(s/def ::post-request-body (s/keys :req-un [::name
                                       ::type
                                       ::slug
                                       ::quote]))

(s/def ::post-response-body (s/keys :req-un [::id
                                             ::created-at
                                             ::name
                                             ::type
                                             ::slug
                                             ::quote]))

(s/def ::get-response-body (s/coll-of ::post-response-body))

(s/def ::dto (s/keys :req-un [::name
                              ::type
                              ::slug
                              ::model/quote]))

(s/def ::cryptocurrency (s/keys :req-un [::model/id
                                         ::model/created-at
                                         ::name
                                         ::type
                                         ::slug
                                         ::model/quote]))

