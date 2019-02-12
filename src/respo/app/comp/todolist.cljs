
(ns respo.app.comp.todolist
  (:require [clojure.string :as string]
            [respo.core
             :refer
             [defcomp div span input <> cursor-> list-> action-> mutation->]]
            [hsl.core :refer [hsl]]
            [respo.app.comp.task :refer [comp-task]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.comp.zero :refer [comp-zero]]
            [respo.app.comp.wrap :refer [comp-wrap]]
            [respo.util.dom :refer [text-width]]
            [respo.app.style.widget :as widget]))

(defn handle-add [state]
  (fn [e dispatch! mutate!] (dispatch! :add (:draft state)) (mutate! (assoc state :draft ""))))

(def initial-state {:draft "", :locked? false})

(defn on-focus [e dispatch!] (println "Just focused~"))

(defn run-test! [dispatch! acc]
  (let [started (.valueOf (js/Date.))]
    (dispatch! :clear nil)
    (loop [x 200] (dispatch! :add "empty") (if (> x 0) (recur (dec x))))
    (loop [x 20] (dispatch! :hit-first (rand)) (if (> x 0) (recur (dec x))))
    (dispatch! :clear nil)
    (loop [x 10] (dispatch! :add "only 10 items") (if (> x 0) (recur (dec x))))
    (let [cost (- (.valueOf (js/Date.)) started)]
      (if (< (count acc) 40)
        (js/setTimeout (fn [] (run-test! dispatch! (conj acc cost))) 0)
        (println "result:" (vec (sort acc)))))))

(defn on-test [e dispatch!] (println "trigger test!") (run-test! dispatch! []))

(def style-list {:color :black, :background-color (hsl 120 20 98)})

(def style-panel {:display :flex, :margin-bottom 4})

(def style-root
  {:color :black,
   :background-color (hsl 120 20 98),
   :line-height "24px",
   "font-size" 16,
   :padding 10,
   :font-family "\"微软雅黑\", Verdana"})

(def style-toolbar
  {:display :flex,
   :flex-direction :row,
   :justify-content :start,
   :padding "4px 0",
   :white-space :nowrap})

(defcomp
 comp-todolist
 (states tasks)
 (let [state (or (:data states) initial-state)]
   (div
    {:style style-root}
    (comp-inspect "States" state {:left "80px"})
    (div
     {:style style-panel}
     (input
      {:placeholder "Text",
       :value (:draft state),
       :style (merge
               widget/input
               {:width (max 200 (+ 24 (text-width (:draft state) 16 "BlinkMacSystemFont")))}),
       :on-input (mutation-> (assoc state :draft (:value %e))),
       :on-focus on-focus})
     (=< 8 nil)
     (span
      {:style widget/button, :on-click (handle-add state)}
      (span {:on-click nil, :inner-text "Add"}))
     (=< 8 nil)
     (span {:inner-text "Clear", :style widget/button, :on-click (action-> :clear nil)})
     (=< 8 nil)
     (div {} (div {:style widget/button, :on-click on-test} (<> "heavy tasks"))))
    (list->
     {:class-name "task-list", :style style-list}
     (->> tasks
          (reverse)
          (map
           (fn [task]
             (let [task-id (:id task)] [task-id (cursor-> task-id comp-task states task)])))))
    (if (> (count tasks) 0)
      (div
       {:spell-check true, :style style-toolbar}
       (div
        {:style widget/button, :on (if (:locked? state) {} {:click (action-> :clear nil)})}
        (<> "Clear2"))
       (=< 8 nil)
       (div
        {:style widget/button, :on-click (mutation-> (update state :locked? not))}
        (<> (str "Lock?" (:locked? state))))
       (=< 8 nil)
       (comp-wrap (comp-zero))))
    (comp-inspect "Tasks" tasks {:left 500, :top 20}))))
