
(ns respo.app.comp.wrap (:require [respo.macros :refer [defcomp div]]))

(defcomp comp-wrap (x) (div {} x))
