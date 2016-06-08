
(ns respo.main
  (:require [respo.component.todolist :refer [todolist-component]]
            [respo.render.expander :refer [render-app]]
            [respo.update.core :refer [update-transform]]
            [respo.render.differ :refer [find-element-diffs]]
            [respo.util.time :refer [io-get-time]]
            [respo.controller.deliver :refer [build-deliver-event
                                              mutate-factory]]
            [respo.controller.resolver :refer [get-markup-at]]
            [respo.util.format :refer [purify-element]]))

(defonce todolist-store
 (atom [{:id 101, :text "101"} {:id 102, :text "102"}]))

(defonce global-states (atom {}))

(defonce global-element (atom nil))

(defonce clients-list (atom []))

(defonce id-counter (atom 10))

(defn dispatch [dispatch-type dispatch-data]
  (println "dispatch:" dispatch-type (pr-str dispatch-data))
  (reset! id-counter (inc @id-counter))
  (let [op-id @id-counter
        new-store (update-transform
                    @todolist-store
                    dispatch-type
                    dispatch-data
                    op-id)]
    (println "new store:" (pr-str new-store))
    (reset! todolist-store new-store)))

(def build-mutate (mutate-factory global-element global-states))

(defn mount-demo []
  (let [todo-demo (todolist-component {:tasks @todolist-store})
        element (render-app todo-demo @global-states build-mutate)]
    (comment
      println
      "store to mount:"
      (pr-str @todolist-store)
      (pr-str (purify-element element)))
    (reset! global-element element)))

(defn rerender-demo []
  (let [todo-demo (todolist-component {:tasks @todolist-store})
        element (render-app todo-demo @global-states build-mutate)
        changes (find-element-diffs
                  []
                  []
                  (purify-element @global-element)
                  (purify-element element))]
    (reset! global-element element)
    (println "states:" @global-states)))

(defn -main []
  (enable-console-print!)
  (println "App is running...")
  (mount-demo)
  (add-watch todolist-store :rerender rerender-demo)
  (add-watch global-states :rerender rerender-demo))

(set! *main-cli-fn* -main)

(defn on-jsload [] (println "reload!") (rerender-demo))
