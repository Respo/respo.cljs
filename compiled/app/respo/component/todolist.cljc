
(ns respo.component.todolist
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.component.task :refer [task-component]]
            [respo.alias :refer [div span input create-comp]]
            [respo.component.zero :refer [component-zero]]
            [respo.component.debug :refer [comp-debug]]))

(def style-root
 {:line-height "24px",
  :color "black",
  :font-size "16px",
  :background-color (hsl 120 20 93),
  :padding "10px"})

(def style-list {:color "black", :background-color (hsl 120 20 96)})

(def style-input
 {:line-height "24px",
  :min-width "300px",
  :font-size "16px",
  :padding "0px 8px",
  :outline "none"})

(def style-toolbar
 {:width "300px",
  :padding "4px 0",
  :justify-content "center",
  :display "flex",
  :flex-direction "row"})

(def style-button
 {:color (hsl 0 0 100),
  :margin-left "8px",
  :background-color (hsl 0 80 70),
  :cursor "pointer",
  :padding "0 6px 0 6px",
  :display "inline-block",
  :border-radius "4px",
  :font-family "Verdana"})

(def style-panel {:display "flex"})

(defn clear-done [props state]
  (fn [event dispatch]
    (println "dispatch clear-done")
    (dispatch :clear nil)))

(defn on-focus [props state]
  (fn [event dispatch] (println "Just focused~")))

(defn on-text-change [props state mutate]
  (fn [simple-event dispatch] (mutate {:draft (:value simple-event)})))

(defn handle-add [props state mutate]
  (comment println "state built inside:" (pr-str props) (pr-str state))
  (fn [event dispatch]
    (comment println "click add!" (pr-str props) (pr-str state))
    (dispatch :add (:draft state))
    (mutate {:draft ""})))

(defn init-state [props] {:draft ""})

(defn update-state [old-state changes]
  (comment println "changes:" (pr-str old-state) (pr-str changes))
  (merge old-state changes))

(defn render [props]
  (fn [state mutate]
    (let [tasks (:tasks props)]
      (div
        {:style style-root}
        (comment comp-debug state {:left "80px"})
        (div
          {:style style-panel}
          (input
            {:style style-input,
             :event
             {:focus (on-focus props state),
              :input (on-text-change props state mutate)},
             :attrs {:placeholder "Text", :value (:draft state)}})
          (span
            {:style style-button}
            (span
              {:event {:click (handle-add props state mutate)},
               :attrs {:inner-text "Add"}}))
          (span
            {:style style-button,
             :event {:click (clear-done props state)},
             :attrs {:inner-text "Clear"}}))
        (div
          {:style style-list, :attrs {:class-name "task-list"}}
          (->>
            tasks
            (map
              (fn [task] [(:id task) (task-component {:task task})]))))
        (if (> (count tasks) 0)
          (div
            {:style style-toolbar, :attrs {:spell-check true}}
            (div
              {:style style-button,
               :event {:click (clear-done props state)}}
              (span {:attrs {:inner-text "Clear2"}}))))
        (comment comp-debug props {})))))

(def comp-todolist
 (create-comp :todolist init-state update-state render))
