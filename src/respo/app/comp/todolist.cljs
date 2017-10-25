
(ns respo.app.comp.todolist
  (:require [clojure.string :as string]
            [respo.macros :refer [defcomp div span input <> cursor->]]
            [hsl.core :refer [hsl]]
            [respo.app.comp.task :refer [comp-task]]
            [respo.comp.space :refer [=<]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.comp.zero :refer [comp-zero]]
            [respo.app.comp.wrap :refer [comp-wrap]]
            [polyfill.core :refer [text-width* io-get-time* set-timeout*]]
            [respo.app.style.widget :as widget]))

(defn clear-done [e dispatch!] (println "dispatch clear-done") (dispatch! :clear nil))

(defn handle-add [state]
  (fn [e dispatch! mutate!] (dispatch! :add (:draft state)) (mutate! (assoc state :draft ""))))

(def style-root
  {:color :black,
   :background-color (hsl 120 20 98),
   :line-height "24px",
   :font-size 16,
   :padding 10,
   :font-family "\"微软雅黑\", Verdana"})

(def style-list {:color :black, :background-color (hsl 120 20 98)})

(def style-toolbar
  {:display :flex,
   :flex-direction :row,
   :justify-content :start,
   :padding "4px 0",
   :white-space :nowrap})

(def style-panel {:display :flex, :margin-bottom 4})

(defn on-test [e dispatch!]
  (println "trigger test!")
  (dispatch! :clear nil)
  (let [started (io-get-time*)]
    (loop [x 200] (dispatch! :add "empty") (if (> x 0) (recur (dec x))))
    (loop [x 20] (dispatch! :hit-first (rand)) (if (> x 0) (recur (dec x))))
    (dispatch! :clear nil)
    (loop [x 10] (dispatch! :add "only 10 items") (if (> x 0) (recur (dec x))))
    (println "time cost:" (- (io-get-time*) started))))

(defn on-focus [e dispatch!] (println "Just focused~"))

(def initial-state {:draft "", :locked? false})

(defn on-text-change [state]
  (fn [e dispatch! mutate!] (mutate! (assoc state :draft (:value e)))))

(defn on-lock [state] (fn [e dispatch! mutate!] (mutate! (update state :locked? not))))

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
               {:width (max 200 (+ 24 (text-width* (:draft state) 16 "BlinkMacSystemFont")))}),
       :on {:input (on-text-change state), :focus on-focus}})
     (=< 8 nil)
     (span {:style widget/button, :on {:click (handle-add state)}} (<> "Add"))
     (=< 8 nil)
     (span {:inner-text "Clear", :style widget/button, :on {:click clear-done}})
     (=< 8 nil)
     (div {} (div {:style widget/button, :on {:click on-test}} (<> "heavy tasks"))))
    (div
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
        {:style widget/button, :on (if (:locked? state) {} {:click clear-done})}
        (<> "Clear2"))
       (=< 8 nil)
       (div
        {:style widget/button, :on {:click (on-lock state)}}
        (<> (str "Lock?" (:locked? state))))
       (=< 8 nil)
       (comp-wrap (comp-zero))))
    (comp-inspect "Tasks" tasks {:left 500, :top 20}))))
