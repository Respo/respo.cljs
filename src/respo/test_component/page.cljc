
(ns respo.test-component.page
  (:require [respo.alias :refer [create-comp div html head body meta' link script style]]))

(def comp-page (create-comp :page (fn [store] (fn [state mutate] (div {})))))
