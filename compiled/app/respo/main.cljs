
(ns respo.main
  (:require [respo.core :refer [render]]
            [respo.schema :as schema]
            [respo.updater.core :refer [updater]]
            [respo.component.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]))

(defonce global-store
 (atom
   (or
     (let [raw (or (.getItem js/localStorage "respo") "[]")]
       (read-string raw))
     schema/store)))

(defonce global-states (atom {}))

(defn dispatch [op op-data]
  (let [op-id (.valueOf (js/Date.))
        new-store (updater @global-store op op-data op-id)]
    (reset! global-store new-store)))

(defn render-app []
  (let [target (.querySelector js/document "#app")]
    (comment println "store:" @global-store)
    (comment println "states:" @global-states)
    (render
      (comp-container @global-store)
      target
      dispatch
      global-states)))

(defn -main []
  (enable-console-print!)
  (println "main...")
  (render-app)
  (add-watch global-store :rerender render-app)
  (add-watch global-states :rerender render-app))

(set! (.-onload js/window) -main)

(defn save-store []
  (.setItem js/localStorage "respo" (pr-str @global-store)))

(set! (.-onbeforeunload js/window) save-store)

(defn on-jsload [] (render-app) (.log js/console "code updated."))
