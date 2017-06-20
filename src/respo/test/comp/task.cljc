
(ns respo.test.comp.task
  (:require-macros (respo.macros :refer (div span)))
  (:require [respo.core :refer [create-comp]] [hsl.core :refer [hsl]]))

(def comp-task
  (create-comp :task (fn [task] (fn [cursor] (div {} (span {:inner-text (:text task)}))))))
