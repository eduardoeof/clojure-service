(ns clojure-service.controller
  (:require [clojure-service.logic.cryptocurrency :as logic]))

(defn ^:dynamic create-cryptocurrency [dto]
  (logic/create-cryptocurrency dto))

 
