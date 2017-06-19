
(ns respo.test-component.page
  (:require-macros (respo.macros :refer (div html head body meta' link script style)))
  (:require [respo.alias :refer [create-comp]]))

(def comp-page (create-comp :page (fn [store] (fn [cursor] (div {})))))
