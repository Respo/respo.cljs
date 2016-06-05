
(ns respo.util.time
  (:require [clojure.string :as string]))

(defn io-get-time [] (quot (System/currentTimeMillis) 1000))
