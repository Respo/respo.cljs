
(ns respo.app.core
  (:require (respo.app.comp.container :refer (comp-container))
            (respo.core :refer (render!))
            (respo.app.schema :as schema)
            (respo.app.updater.core :refer (updater))
            (respo.util.id :refer (get-id!))))

(def *store (atom schema/store))

(defn dispatch! [op op-data]
  (comment println op)
  (let [op-id (get-id!), store (updater @*store op op-data op-id)] (reset! *store store)))

(defn render-app! [mount-target] (render! mount-target (comp-container @*store) dispatch!))
