
(ns respo.app.comp.container
  (:require [respo.core :refer [defcomp div span <> >>]]
            [respo.app.comp.todolist :refer [comp-todolist]]
            [respo.app.comp.caches :refer [comp-caches]]
            [respo.comp.space :refer [=<]]))

(def style-global {:font-family "Avenir,Verdana"})

(def style-states {:padding 8})

(defcomp
 comp-container
 (store)
 (let [states (:states store)]
   (div
    {:style style-global}
    (comp-todolist states (:tasks store))
    (div {:style style-states} (<> (str "states: " (pr-str (:states store)))))
    (=< nil 40)
    (comp-caches (>> states :caches)))))
