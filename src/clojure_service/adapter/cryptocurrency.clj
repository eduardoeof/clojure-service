(ns clojure-service.adapter.cryptocurrency
  (:require [java-time :as time]
            [clojure.spec.alpha :as s]
            [clojure-service.schema.cryptocurrency :as schema]))

(def date-time-format "yyyy-MM-dd'T'HH:mm:ss.SSS")

;; TODO: Use destructuring 
(defn request-body->dto 
  [{:keys [name type slug] 
          {:keys [USD BTC]} :quote 
          :as body}]
  {:pre  [(s/valid? ::schema/request-body body)]
   :post [(s/valid? ::schema/dto %)]}
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
  [{:keys [id name type slug created-at] 
          {:keys [USD BTC]} :quote 
          :as cryptocurrency}]
  {:pre  [(s/valid? ::schema/cryptocurrency cryptocurrency)]
   :post [(s/valid? ::schema/response-body %)]}
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

(defn mongodb-document->cryptocurrency [document]
  {:post [(s/valid? ::schema/cryptocurrency %)]}
  (dissoc document :_id))

