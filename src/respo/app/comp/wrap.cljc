
(ns respo.app.comp.wrap
  (:require-macros [respo.macros :refer [defcomp div span->]])
  (:require [respo.core]))

(defcomp comp-wrap (x) (div {} x))
