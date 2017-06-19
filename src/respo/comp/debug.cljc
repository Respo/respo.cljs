
(ns respo.comp.debug
  (:require-macros (respo.macros :refer (div span)))
  (:require [hsl.core :refer [hsl]] [respo.alias :refer [create-comp]]))

(def default-style
  {:position :absolute,
   :background-color (hsl 0 0 0),
   :color :white,
   :opacity 0.4,
   :font-size "10px",
   :font-family "Menlo",
   :box-shadow (str "0 0 1px " (hsl 0 0 0 0.8)),
   :line-height 1.6,
   :padding "2px 4px",
   :pointer-events :none})

(def comp-debug
  (create-comp
   :debug
   (fn [data more-style]
     (fn [cursor]
       (div {:style (merge default-style more-style)} (span {:inner-text (pr-str data)}))))))
