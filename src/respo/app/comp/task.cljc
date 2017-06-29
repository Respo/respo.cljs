
(ns respo.app.comp.task
  (:require-macros [respo.macros :refer [defcomp div input span button <>]])
  (:require [hsl.core :refer [hsl]]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.style.widget :as widget]))

(def style-task {:display :flex, :padding "4px 0px"})

(defn on-click [props state] (fn [event dispatch!] (println "clicked.")))

(defn handle-done [task-id] (fn [e dispatch!] (dispatch! :toggle task-id)))

(def style-done
  {:width 32, :height 32, :outline :none, :border :none, :vertical-align :middle})

(defn on-text-change [task]
  (fn [event dispatch!]
    (let [task-id (:id task), text (:value event)]
      (dispatch! :update {:id task-id, :text text}))))

(defn handle-remove [task] (fn [e dispatch!] (dispatch! :remove (:id task))))

(defn on-text-state [e dispatch! mutate!] (mutate! (:value e)))

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
      :on {:click (handle-done (:id task))}})
    (=< 8 nil)
    (input {:value (:text task), :style widget/input, :on {:input (on-text-change task)}})
    (=< 8 nil)
    (input {:value state, :style widget/input, :on {:input on-text-state}})
    (=< 8 nil)
    (div {:style widget/button, :on {:click (handle-remove task)}} (<> span "Remove" nil))
    (=< 8 nil)
    (div {} (<> span state nil)))))
