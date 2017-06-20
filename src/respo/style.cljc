
(ns respo.style (:require [hsl.core :refer [hsl]]))

(def style-value
  {:position :absolute,
   :background-color (hsl 0 0 0),
   :color :white,
   :opacity 0.4,
   :font-size "10px",
   :font-family "Menlo",
   :line-height 1.6,
   :padding "2px 4px",
   :pointer-events :none})
