
(ns respo.client
  (:require [respo.controller.client :refer [initialize-instance
                                             activate-instance
                                             patch-instance
                                             release-instance]]
            [respo.util.time :refer [io-get-time]]))

(defn deliver-event [coord event-name simple-event])

(defn mount-demo []
  (let [app-root (.querySelector js/document "#app")]
    (initialize-instance app-root deliver-event)))

(defn -main []
  (enable-console-print!)
  (.log js/console "App is running...")
  (mount-demo))

(set! js/window.onload -main)

(defn on-jsload []
  (.clear js/console)
  (let [app-root (.querySelector js/document "#app")]
    (.info js/console "Reloaded!")))
