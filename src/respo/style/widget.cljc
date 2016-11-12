
(ns respo.style.widget (:require [hsl.core :refer [hsl]]))

(def input
  {:line-height "24px",
   :min-width "300px",
   :font-size "16px",
   :background-color (hsl 0 0 94),
   :padding "0px 8px",
   :outline :none,
   :border :none})

(def button
  {:line-height "28px",
   :color (hsl 0 0 100),
   :background-color (hsl 0 80 70),
   :cursor :pointer,
   :padding "0 6px 0 6px",
   :display :inline-block,
   :font-family "Avenir,Verdana",
   :height 28})
