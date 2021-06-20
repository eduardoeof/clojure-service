(ns clojure-service.adapter.cryptocurrency
  (:require [java-time :as time]
            [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency.model :as schema.model]
            [clojure-service.schema.cryptocurrency.dto :as schema.dto]))

(def utc-zone-id (time/zone-id "Z"))
(def date-time-format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

(defn- zoned-date-time->str [zoned-date-time]
  (time/format date-time-format zoned-date-time))

(defn- instant->zoned-date-time [instant]
  (time/zoned-date-time instant utc-zone-id))

(defn- zoned-date-time->instant [zoned-date-time]
  (.toInstant zoned-date-time))

(defn- assoc-if
  ([m key value]
   (assoc-if m key value nil))
  ([m key value f]
   (if value
     (assoc m key (if f 
                    (f value) 
                    value))
     m)))

(defn- cryptocurrency->json
  [{:keys [id name type slug created-at] 
          {:keys [USD BTC]} :quote :as cryptocurrency}]
  {:id (str id)
   :name name
   :type type
   :slug slug
   :created-at (zoned-date-time->str created-at) 
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (->> USD :last-updated zoned-date-time->str)}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (->> BTC :last-updated zoned-date-time->str)
                 :volume-24h         (:volume-24h BTC)}}})

(defn request-body->cryptocurrency 
  [{:keys [name type slug] {:keys [USD BTC]} :quote :as body}]
  {:pre  [(s/valid? ::schema.dto/request-body body)]
   :post [(s/valid? ::schema.model/cryptocurrency %)]}
  {:name name
   :type type
   :slug slug
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (-> USD :last-updated time/zoned-date-time)}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (-> BTC :last-updated time/zoned-date-time)
                 :volume-24h         (:volume-24h BTC)}}})

(defn mongodb-document->cryptocurrency 
  [{:keys [id name type slug created-at] {:keys [USD BTC]} :quote :as _document}]
  {:post [(s/valid? ::schema.model/cryptocurrency %)]}
  {:id id 
   :name name
   :type type
   :slug slug
   :created-at (instant->zoned-date-time created-at)
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (-> USD :last-updated instant->zoned-date-time)}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (-> BTC :last-updated instant->zoned-date-time)
                 :volume-24h         (:volume-24h BTC)}}})

(defn cryptocurrency->mongodb-document 
  [{:keys [id name type slug created-at] {:keys [USD BTC]} :quote :as cryptocurrency}]
  {:pre [(s/valid? ::schema.model/cryptocurrency cryptocurrency)]}
  {:id id 
   :name name
   :type type
   :slug slug
   :created-at (zoned-date-time->instant created-at)
   :quote {:USD {:price              (:price USD)
                 :percent-change-1h  (:percent-change-1h USD)
                 :percent-change-24h (:percent-change-24h USD)
                 :percent-change-7d  (:percent-change-7d USD)
                 :last-updated       (-> USD :last-updated zoned-date-time->instant)}
           :BTC {:price              (:price BTC)
                 :percent-change-1h  (:percent-change-1h BTC)
                 :percent-change-24h (:percent-change-24h BTC)
                 :percent-change-7d  (:percent-change-7d BTC)
                 :last-updated       (-> BTC :last-updated zoned-date-time->instant)
                 :volume-24h         (:volume-24h BTC)}}})

(defn cryptocurrency->response-body 
  [cryptocurrency]
  {:pre  [(s/valid? ::schema.model/cryptocurrency cryptocurrency)]
   :post [(s/valid? ::schema.dto/post-response-body %)]}
  {:cryptocurrency (cryptocurrency->json cryptocurrency)})

(defn cryptocurrencies->response-body
  [cryptocurrencies]
  {:pre  [(s/valid? ::schema.model/cryptocurrencies cryptocurrencies)] 
   :post [(s/valid? ::schema.dto/get-response-body %)]}
  {:cryptocurrencies (map cryptocurrency->json cryptocurrencies)})

(defn query-and-path-params->params
  [{:keys [type] :as query-params}
   {:keys [id] :as path-params}]
  (-> {}
      (assoc-if :id id #(java.util.UUID/fromString %))
      (assoc-if :type type)))

