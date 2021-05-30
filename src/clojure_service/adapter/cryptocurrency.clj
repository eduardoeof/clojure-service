(ns clojure-service.adapter.cryptocurrency
  (:require [java-time :as time]
            [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency.model :as schema.model]
            [clojure-service.schema.cryptocurrency.dto :as schema.dto]
            [clojure-service.schema.cryptocurrency :as schema]))

(def utc-zone-id (time/zone-id "UTC"))
(def date-time-format "yyyy-MM-dd'T'HH:mm:ss.SSS")

(defn- cryptocurrency->json
  [{:keys [id name type slug created-at] 
          {:keys [USD BTC]} :quote :as cryptocurrency}]
  {:id (str id)
   :name name
   :type type
   :slug slug
   :created-at (time/format created-at)
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (->> USD :last-updated (time/format date-time-format))}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (->> BTC :last-updated (time/format date-time-format))
                 :volume-24h         (:volume-24h BTC)}}})

(defn request-body->cryptocurrency 
  [{:keys [name type slug] {:keys [USD BTC]} :quote :as body}]
  {:pre  [(s/valid? ::schema/request-body body)]
   :post [(s/valid? ::schema.model/cryptocurrency %)]}
  {:name name
   :type type
   :slug slug
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (-> USD :last-updated time/local-date-time)}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (-> BTC :last-updated time/local-date-time)
                 :volume-24h         (:volume-24h BTC)}}})

(defn cryptocurrency->response-body 
  [cryptocurrency]
  {:pre  [(s/valid? ::schema.model/cryptocurrency cryptocurrency)]
   :post [(s/valid? ::schema.dto/post-response-body %)]}
  {:cryptocurrency (cryptocurrency->json cryptocurrency)})

(defn mongodb-document->cryptocurrency 
  [{:keys [id name type slug created-at] {:keys [USD BTC]} :quote :as _document}]
  {:post [(s/valid? ::schema/cryptocurrency %)]}
  {:id id 
   :name name
   :type type
   :slug slug
   :created-at (time/local-date-time created-at utc-zone-id)
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (-> USD :last-updated (time/local-date-time utc-zone-id))}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (-> BTC :last-updated (time/local-date-time utc-zone-id))
                 :volume-24h         (:volume-24h BTC)}}})

(defn cryptocurrencies->response-body
  [cryptocurrencies]
  {:pre  [(s/valid? ::schema.model/cryptocurrencies cryptocurrencies)] 
   :post [(s/valid? ::schema.dto/get-response-body %)]}
  {:cryptocurrencies (map cryptocurrency->json cryptocurrencies)})

