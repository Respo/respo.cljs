
(ns respo.app.comp.caches
  (:require [respo.core :refer [defcomp defplugin div button span >>]]
            [respo.comp.space :refer [=<]]
            [respo.app.style.widget :as widget]
            [respo.caches :as caches]))

(defplugin
 use-demo
 (states)
 (let [cursor (states :cursor), state (or (:data states) {:status true})]
   {:ui (span {:inner-text (str "status: " (state :status))}),
    :toggle (fn [d!] (d! cursor (update state :status not)))}))

(defcomp
 comp-caches
 (states)
 (let [value-plugin (use-demo (>> states :count))]
   (div
    {:style {:padding 8}}
    (div
     {}
     (div
      {:inner-text "Loop",
       :style widget/button,
       :on-click (fn [e d!] (caches/new-loop!) (println @caches/*cache-states))})
     (=< 8 nil)
     (div
      {:inner-text "Add cache",
       :style widget/button,
       :on-click (fn [e d!] (caches/write-cache! [1 2 3] 6))})
     (=< 8 nil)
     (div
      {:inner-text "Access",
       :style widget/button,
       :on-click (fn [e d!] (println (caches/access-cache [1 2 3])))})
     (=< 8 nil)
     (div
      {:inner-text "Reset",
       :style widget/button,
       :on-click (fn [e d!] (caches/reset-caches!))})
     (=< 8 nil)
     (div
      {:inner-text "GC", :style widget/button, :on-click (fn [e d!] (caches/perform-gc!))}))
    (=< nil 8)
    (div
     {}
     (div
      {:inner-text "Trigger",
       :style widget/button,
       :on-click (fn [e d!] ((:toggle value-plugin) d!))}))
    (:ui value-plugin))))
