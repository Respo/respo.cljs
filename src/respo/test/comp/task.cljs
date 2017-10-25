
(ns respo.test.comp.task
  (:require [hsl.core :refer [hsl]] [respo.macros :refer (defcomp div span)]))

(defcomp comp-task (task) (div {} (span {:inner-text (:text task)})))
