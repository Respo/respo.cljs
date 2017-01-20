
(ns respo.test-component.task
  (:require [respo.alias :refer [div span create-comp]] [hsl.core :refer [hsl]]))

(def comp-task
  (create-comp
   :task
   (fn [task] (fn [state mutate] (div {} (span {:attrs {:inner-text (:text task)}}))))))
