
(ns respo.main
  (:require [respo.core :refer [clear-cache! *changes-logger]]
            [cljs.reader :refer [read-string]]
            [respo.app.core :refer [render-app! *store]]))

(def mount-target (.querySelector js/document "#app"))

(defn main! []
  (let [raw (.getItem js/window.localStorage "respo")]
    (if (some? raw)
      (swap! *store assoc :tasks (read-string raw))))
  (render-app! mount-target)
  (add-watch *store :rerender
    (fn [] (render-app! mount-target)))
  ; (reset! *changes-logger (fn [old-tree new-tree changes]
  ;                             (.log js/console (clj->js changes))))
  )

(set! (.-onload js/window) main!)

(defn save-store! []
  (.setItem js/window.localStorage "respo" (pr-str (:tasks @*store))))

(set! (.-onbeforeunload js/window) save-store!)

(defn reload! []
  (clear-cache!)
  (render-app! mount-target)
  (.log js/console "code updated."))
