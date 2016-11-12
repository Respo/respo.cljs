
(ns respo.comp.zero (:require [respo.alias :refer [create-comp div span]]))

(defn render [] (fn [state mutate] (div {:attrs {:inner-text 0}})))

(def component-zero (create-comp :zero render))
