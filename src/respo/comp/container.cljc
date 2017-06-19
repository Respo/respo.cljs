
(ns respo.comp.container
  (:require-macros (respo.macros :refer (div2)))
  (:require (respo.alias :refer (create-comp div))
            (respo.cursor :refer (with-cursor))
            (respo.comp.text :refer (comp-text))
            (respo.comp.todolist :refer (comp-todolist))))

(def style-states {:padding 8})

(def comp-container
  (create-comp
   :container
   (fn [store]
     (fn [cursor]
       (let [state (:states store)]
         (println "First" (div2 {} (div {}) (div {})))
         (println "Second" (div2 {} (div {})))
         (div
          {}
          (with-cursor :todolist (comp-todolist (:todolist state) (:tasks store)))
          (div {:style style-states} (comp-text (pr-str (:states store)) nil))))))))
