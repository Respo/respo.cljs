
(ns respo.app.comp.container
  (:require-macros (respo.macros :refer (div)))
  (:require (respo.core :refer (create-comp))
            (respo.cursor :refer (with-cursor))
            (respo.comp.text :refer (comp-text))
            (respo.app.comp.todolist :refer (comp-todolist))))

(def style-states {:padding 8})

(def comp-container
  (create-comp
   :container
   (fn [store]
     (fn [cursor]
       (let [state (:states store)]
         (div
          {}
          (with-cursor :todolist (comp-todolist (:todolist state) (:tasks store)))
          (div {:style style-states} (comp-text (pr-str (:states store)) nil))))))))
