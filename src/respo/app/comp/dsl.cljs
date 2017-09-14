
(ns respo.app.comp.dsl
  (:require-macros [respo.macros :refer [defcomp div span <>]])
  (:require [respo.core :refer [create-comp create-element]] [respo.comp.space :refer [=<]]))

(defcomp
 comp-dsl
 ()
 [:div
  "DSL Example"
  [:div.demo.demo2 "class-name demo"]
  [:div#x1 "id demo"]
  [:div {:class-name "demo2 demo3"} "prop demo"]
  (div {} (<> "nested") (=< 8 nil) [:span "example"])])
