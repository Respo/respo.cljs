
(ns respo.test-component.task
  (:require [respo.alias :refer [div span create-comp]] [hsl.core :refer [hsl]]))

(defn render [task] (fn [state mutate] (div {} (span {:attrs {:inner-text (:text task)}}))))

(def comp-task (create-comp :task render))
