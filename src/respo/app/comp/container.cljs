
(ns respo.app.comp.container
  (:require [respo.macros :refer [defcomp div span <> cursor->]]
            [respo.app.comp.todolist :refer [comp-todolist]]
            [respo.app.comp.svg-example :refer [comp-svg-example]]))

(def style-global {:font-family "Avenir,Verdana"})

(def style-states {:padding 8})

(defcomp
 comp-container
 (store)
 (let [state (:states store)]
   (div
    {:style style-global}
    (cursor-> :todolist comp-todolist state (:tasks store))
    (div {:style style-states} (<> (str "states: " (pr-str (:states store)))))
    (comp-svg-example))))
