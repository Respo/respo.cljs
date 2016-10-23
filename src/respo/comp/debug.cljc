
(ns respo.comp.debug
  (:require [hsl.core :refer [hsl]] [respo.alias :refer [create-comp div span]]))

(def default-style
 {:line-height 1.6,
  :box-shadow (str "0 0 1px " (hsl 0 0 0 0.8)),
  :color "white",
  :font-size "10px",
  :background-color (hsl 0 0 0),
  :opacity 0.4,
  :padding "2px 4px",
  :position "absolute",
  :pointer-events "none",
  :font-family "Menlo"})

(defn render [data more-style]
  (fn [state mutate!]
    (div
      {:style (merge default-style more-style)}
      (span {:attrs {:inner-text (pr-str data)}}))))

(def comp-debug (create-comp :debug render))
