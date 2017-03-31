
(ns respo.main
  (:require [respo.core :refer [render! clear-cache!]]
            [respo.schema :as schema]
            [respo.updater.core :refer [updater]]
            [respo.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [devtools.core :as devtools]))

(defonce global-store
 (atom
   (or
     (let [raw (or (.getItem js/localStorage "respo") "[]")]
       {:tasks (read-string raw) :states {}})
     schema/store)))

(defonce id-ref (atom 0))

(defn id! []
  (swap! id-ref inc)
  @id-ref)

(defn dispatch! [op op-data]
  (let [op-id (id!)
        new-store (updater @global-store op op-data op-id)]
    (reset! global-store new-store)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (comment println "store:" @global-store)
    (render!
      (comp-container @global-store)
      target
      dispatch!)))

(defn -main []
  (enable-console-print!)
  (devtools/install!)
  (render-app!)
  (add-watch global-store :rerender render-app!))

(set! (.-onload js/window) -main)

(defn save-store! []
  (.setItem js/localStorage "respo" (pr-str (:tasks @global-store))))

(set! (.-onbeforeunload js/window) save-store!)

(defn on-jsload []
  (clear-cache!)
  (render-app!)
  (.log js/console "code updated."))
