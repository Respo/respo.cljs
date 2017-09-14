
(ns respo.test.comp.task
  (:require-macros (respo.macros :refer (defcomp div span)))
  (:require [hsl.core :refer [hsl]] [respo.core :refer [create-comp create-element]]))

(defcomp comp-task (task) (div {} (span {:inner-text (:text task)})))
