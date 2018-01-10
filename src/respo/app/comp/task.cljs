
(ns respo.app.comp.task
  (:require [respo.macros :refer [defcomp div input span button <> action-> mutation->]]
            [hsl.core :refer [hsl]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.style.widget :as widget]))

(defn on-text-change [task]
  (fn [event dispatch!]
    (let [task-id (:id task), text (:value event)]
      (dispatch! :update {:id task-id, :text text}))))

(def style-done
  {:width 32, :height 32, :outline :none, :border :none, :vertical-align :middle})

(def style-task {:display :flex, :padding "4px 0px"})

(defcomp
 comp-task
 (states task)
 (let [state (or (:data states) "")]
   (div
    {:style style-task}
    (comp-inspect "Task" task {:left 200})
    (button
     {:style (merge
              style-done
              {:background-color (if (:done? task) (hsl 200 20 80) (hsl 200 80 70))}),
      :on-click (action-> :toggle (:id task))})
    (=< 8 nil)
    (input {:value (:text task), :style widget/input, :on-input (on-text-change task)})
    (=< 8 nil)
    (input {:value state, :style widget/input, :on-input (mutation-> (:value %e))})
    (=< 8 nil)
    (div {:style widget/button, :on-click (action-> :remove (:id task))} (<> "Remove"))
    (=< 8 nil)
    (div {} (<> state)))))

(defn on-click [props state] (fn [event dispatch!] (println "clicked.")))
