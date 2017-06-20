
(ns respo.app.comp.zero
  (:require-macros (respo.macros :refer (div span)))
  (:require [respo.core :refer [create-comp]]))

(def component-zero (create-comp :zero (fn [] (fn [state mutate] (div {:inner-text 0})))))
