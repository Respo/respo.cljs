
(ns respo.test.comp.task
  (:require [hsl.core :refer [hsl]] [respo.core :refer (defcomp div span)]))

(defcomp comp-task (task) (div {} (span {:inner-text (:text task)})))
