
(ns respo.app.comp.todolist
  (:require-macros (respo.macros :refer (div span input span <> )))
  (:require [clojure.string :as string]
            [hsl.core :refer [hsl]]
            [respo.app.comp.task :refer [comp-task]]
            [respo.core :refer [create-comp]]
            [respo.cursor :refer [with-cursor]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.inspect :refer [comp-inspect]]
            [respo.app.comp.zero :refer [comp-zero]]
            [respo.app.comp.wrap :refer [comp-wrap]]
            [polyfill.core :refer [text-width* io-get-time* set-timeout*]]
            [respo.app.style.widget :as widget]))

(defn clear-done [e dispatch!] (println "dispatch clear-done") (dispatch! :clear nil))

(defn handle-add [cursor state]
  (fn [e dispatch!]
    (dispatch! :add (:draft state))
    (dispatch! :states [cursor (assoc state :draft "")])))

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

(defn on-text-change [cursor state]
  (fn [e dispatch!] (dispatch! :states [cursor (assoc state :draft (:value e))])))

(defn on-lock [cursor state]
  (fn [e dispatch!] (dispatch! :states [cursor (update state :locked? not)])))

(def comp-todolist
  (create-comp
   :todolist
   (fn [states tasks]
     (fn [cursor]
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
                     {:width (max
                              200
                              (+ 24 (text-width* (:draft state) 16 "BlinkMacSystemFont")))}),
             :event {:input (on-text-change cursor state), :focus on-focus}})
           (comp-space 8 nil)
           (span
            {:style widget/button, :event {:click (handle-add cursor state)}}
            (<> span "Add" nil))
           (comp-space 8 nil)
           (span {:inner-text "Clear", :style widget/button, :event {:click clear-done}})
           (comp-space 8 nil)
           (div
            {}
            (div
             {:style widget/button, :event {:click on-test}}
             (<> span "heavy tasks" nil))))
          (div
           {:class-name "task-list", :style style-list}
           (->> tasks
                (reverse)
                (map
                 (fn [task]
                   (let [task-id (:id task)]
                     [task-id (with-cursor task-id (comp-task (get states task-id) task))])))))
          (if (> (count tasks) 0)
            (div
             {:spell-check true, :style style-toolbar}
             (div
              {:style widget/button, :event (if (:locked? state) {} {:click clear-done})}
              (<> span "Clear2" nil))
             (comp-space 8 nil)
             (div
              {:style widget/button, :event {:click (on-lock cursor state)}}
              (<> span (str "Lock?" (:locked? state)) nil))
             (comp-space 8 nil)
             (comp-wrap (comp-zero))))
          (comp-inspect "Tasks" tasks {:left 500, :top 20})))))))
