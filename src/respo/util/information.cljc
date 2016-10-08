
(ns respo.util.information
  (:require [clojure.string :as string]))

(def bubble-events
 [:click
  :dblclick
  :change
  :input
  :keydown
  :keyup
  :wheel
  :mousedown
  :touchstart])

(def no-bubble-events [:focus :blur :scroll])
