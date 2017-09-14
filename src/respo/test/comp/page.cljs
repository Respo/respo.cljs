
(ns respo.test.comp.page
  (:require-macros [respo.macros :refer [div html head body meta' link script style]])
  (:require [respo.core :refer [create-comp create-element]]))

(defcomp comp-page (store) (div {}))
