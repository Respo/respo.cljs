
(ns respo.component.task
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.alias :refer [div input span create-comp button]]
            [respo.component.debug :refer [comp-debug]]
            [respo.component.space :refer [comp-space]]
            [respo.component.text :refer [comp-text]]))

(defn init-state [props] "")

(defn update-state [state text] text)

(def style-task {:display "flex"})

(def style-input
 {:line-height "24px",
  :min-width "200px",
  :font-size "16px",
  :padding "0px 8px",
  :outline "none"})

(def style-button
 {:color (hsl 40 80 100),
  :background-color (hsl 200 80 50),
  :cursor "pointer",
  :padding "0 6px",
  :display "inline-block",
  :border-radius "4px",
  :font-family "Verdana"})

(defn style-done [done?]
  {:vertical-align "middle",
   :background-color (if done? (hsl 200 20 80) (hsl 200 80 70)),
   :width "32px",
   :outline "none",
   :border "none",
   :height "32px"})

(def style-time {:color (hsl 0 0 80)})

(defn on-click [props state]
  (fn [event dispatch!] (println "clicked.")))

(defn handle-remove [task]
  (fn [e dispatch!] (dispatch! :remove (:id task))))

(defn handle-done [task-id]
  (fn [e dispatch!] (dispatch! :toggle task-id)))

(defn on-text-state [e dispatch! mutate!] (mutate! (:value e)))

(defn on-text-change [task]
  (fn [event dispatch!]
    (let [task-id (:id task) text (:value event)]
      (dispatch! :update {:id task-id, :text text}))))

(defn render [task]
  (fn [state mutate!]
    (div
      {:style style-task}
      (comp-debug task {:left "160px"})
      (button
        {:style (style-done (:done? task)),
         :event {:click (handle-done (:id task))}})
      (comp-space 8 nil)
      (input
        {:style style-input,
         :event {:input (on-text-change task)},
         :attrs {:value (:text task)}})
      (comp-space 8 nil)
      (input
        {:style style-input,
         :event {:input on-text-state},
         :attrs {:value state}})
      (comp-space 8 nil)
      (div {} (comp-text state nil))
      (comp-space 8 nil)
      (div
        {:style style-button, :event {:click (handle-remove task)}}
        (comp-text "Remove"))
      (comp-space 8 nil)
      (div
        {:style style-time}
        (comp-text (or (:time state) "none") nil)))))

(def task-component (create-comp :task init-state update-state render))
