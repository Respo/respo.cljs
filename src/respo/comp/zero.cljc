
(ns respo.comp.zero (:require [respo.alias :refer [create-comp div span]]))

(def component-zero (create-comp :zero (fn [] (fn [state mutate] (div {:inner-text 0})))))
