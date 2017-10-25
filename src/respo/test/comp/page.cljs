
(ns respo.test.comp.page
  (:require [respo.macros :refer-macros [div html head body meta' link script style]]))

(defcomp comp-page (store) (div {}))
