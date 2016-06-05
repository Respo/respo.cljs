
(ns respo.util.time
  (:require [clojure.string :as string]))

(defn io-get-time [] (.valueOf (js/Date.)))
