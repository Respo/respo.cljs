
(ns respo.app.comp.wrap (:require [respo.macros :refer-macros [defcomp div]]))

(defcomp comp-wrap (x) (div {} x))
