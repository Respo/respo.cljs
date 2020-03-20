
(ns respo.app.core
  (:require [respo.app.comp.container :refer [comp-container]]
            [respo.core :refer [render! realize-ssr!]]
            [respo.app.schema :as schema]
            [respo.app.updater :refer [updater]]
            [respo.util.id :refer [get-id!]]
            [respo.render.html :refer [make-string]]))

(defonce *store (atom schema/store))

(defn dispatch! [op op-data]
  (comment println op op-data)
  (if (vector? op)
    (recur :states [op op-data])
    (let [op-id (get-id!), store (updater @*store op op-data op-id)] (reset! *store store))))

(defn handle-ssr! [mount-target]
  (realize-ssr! mount-target (comp-container @*store) dispatch!))

(defn render-app! [mount-target] (render! mount-target (comp-container @*store) dispatch!))
