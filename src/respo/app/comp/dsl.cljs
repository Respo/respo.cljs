
(ns respo.app.comp.dsl
  (:require-macros [respo.macros :refer [defcomp div span <>]])
  (:require [respo.core :refer [create-comp create-element]]))

(defcomp
 comp-dsl
 ()
 (div {} (<> "DSL") [:div {:class-name "class"} "ok" "main" "cute" [:div [:div]]]))
