
(ns respo.style.widget (:require [hsl.core :refer [hsl]]))

(def input
  {:font-size "16px",
   :line-height "24px",
   :padding "0px 8px",
   :outline :none,
   :min-width "300px",
   :background-color (hsl 0 0 94),
   :border :none})

(def button
  {:display :inline-block,
   :padding "0 6px 0 6px",
   :font-family "Avenir,Verdana",
   :cursor :pointer,
   :background-color (hsl 0 80 70),
   :color (hsl 0 0 100),
   :height 28,
   :line-height "28px"})
