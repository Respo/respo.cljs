
(ns respo.test.comp.page
  (:require-macros [respo.macros :refer [div html head body meta' link script style]])
  (:require [respo.core :refer [create-comp create-element]]))

(def comp-page (create-comp :page (fn [store] (fn [cursor] (div {})))))
