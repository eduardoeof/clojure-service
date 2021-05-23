(ns integration.util.mongodb
  (:require [monger.collection :as monger]))

(defn drop-collection 
  [collection 
   {:keys [db] :as _mongodb}]
  (monger/drop db collection))

