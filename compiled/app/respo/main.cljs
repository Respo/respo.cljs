
(ns respo.main
  (:require [respo.core :refer [render! clear-cache!]]
            [respo.schema :as schema]
            [respo.updater.core :refer [updater]]
            [respo.component.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [devtools.core :as devtools]))

(defonce global-store
 (atom
   (or
     (let [raw (or (.getItem js/localStorage "respo") "[]")]
       (read-string raw))
     schema/store)))

(defonce global-states (atom {}))

(defn dispatch! [op op-data]
  (let [op-id (.valueOf (js/Date.))
        new-store (updater @global-store op op-data op-id)]
    (reset! global-store new-store)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (comment println "store:" @global-store)
    (comment println "states:" @global-states)
    (js/requestAnimationFrame
      (fn []
        (render!
          (comp-container @global-store)
          target
          dispatch!
          global-states)))))

(defn -main []
  (enable-console-print!)
  (devtools/install!)
  (render-app!)
  (add-watch global-store :rerender render-app!)
  (add-watch global-states :rerender render-app!))

(set! (.-onload js/window) -main)

(defn save-store! []
  (.setItem js/localStorage "respo" (pr-str @global-store)))

(set! (.-onbeforeunload js/window) save-store!)

(defn on-jsload []
  (clear-cache!)
  (render-app!)
  (.log js/console "code updated."))
