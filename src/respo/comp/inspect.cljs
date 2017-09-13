
(ns respo.comp.inspect
  (:require-macros [respo.macros :refer [defcomp pre <>]])
  (:require [respo.core :refer [create-comp create-element]]
            [hsl.core :refer [hsl]]
            [respo.env :refer [data->native]]
            [polyfill.core :refer [log*]]))

(def style-data
  {:position :absolute,
   :background-color (hsl 240 100 0),
   :color :white,
   :opacity 0.2,
   :font-size "12px",
   :font-family "Avenir,Verdana",
   :line-height 1.4,
   :padding "2px 6px",
   :border-radius "4px",
   :max-width 160,
   :max-height 32,
   :white-space :normal,
   :overflow :ellipsis,
   :cursor :default})

(defn on-click [data]
  (fn [e dispatch!]
    (let [raw (pr-str data)] (if (> (count raw) 60) (log* (data->native data)) (log* raw)))))

(defn grab-info [data]
  (cond
    (map? data) (str "Map/" (count data))
    (vector? data) (str "Vector/" (count data))
    (set? data) (str "Set/" (count data))
    (nil? data) "nil"
    (number? data) (str data)
    (keyword? data) (str data)
    (boolean? data) (str data)
    (fn? data) "Fn"
    :else (pr-str data)))

(defcomp
 comp-inspect
 (tip data style)
 (pre
  {:inner-text (str tip ": " (grab-info data)),
   :style (merge style-data style),
   :on {:click (on-click data)}}))
