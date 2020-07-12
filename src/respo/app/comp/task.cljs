
(ns respo.app.comp.task
  (:require [respo.core :refer [defcomp div input span button <> defeffect]]
            [hsl.core :refer [hsl]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.style.widget :as widget]))

(defeffect
 effect-log
 (task)
 (action parent at-place?)
 (comment js/console.log "Task effect" action at-place?)
 (case action
   :mount (let [x0 (js/Math.random)] (comment println "Stored" x0))
   :update (comment println "read")
   :unmount (comment println "read")
   (do)))

(def style-done
  {:width 32, :height 32, :outline :none, :border :none, :vertical-align :middle})

(def style-task {:display :flex, :padding "4px 0px"})

(defcomp
 comp-task
 (states task)
 (let [cursor (:cursor states), state (or (:data states) "")]
   [(effect-log task)
    (div
     {:style style-task}
     (comp-inspect "Task" task {:left 200})
     (button
      {:style (merge
               style-done
               {"background-color" (if (:done? task) (hsl 200 20 80) (hsl 200 80 70))}),
       :on-click (fn [e d!] (d! :toggle (:id task)))})
     (=< 8 nil)
     (input
      {:value (:text task),
       :style widget/input,
       :on-input (fn [e d!]
         (let [task-id (:id task), text (:value e)] (d! :update {:id task-id, :text text})))})
     (=< 8 nil)
     (input
      {:value state, :style widget/input, :on-input (fn [e d!] (d! cursor (:value e)))})
     (=< 8 nil)
     (div
      {:style widget/button, :on-click (fn [e d!] (d! :remove (:id task)))}
      (<> "Remove"))
     (=< 8 nil)
     (div {} (<> state)))]))

(defn on-click [props state] (fn [event dispatch!] (println "clicked.")))
