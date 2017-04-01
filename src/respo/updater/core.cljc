
(ns respo.updater.core (:require [clojure.string :as string]))

(defn updater [old-store op-type op-data op-id]
  (comment println (pr-str old-store) (pr-str op-type) (pr-str op-data))
  (case op-type
    :states
      (let [[cursor new-state] op-data]
        (assoc-in old-store (cons :states (conj cursor :data)) new-state))
    :add
      (update
       old-store
       :tasks
       (fn [tasks] (conj tasks {:text op-data, :id op-id, :done? false})))
    :remove
      (update
       old-store
       :tasks
       (fn [tasks] (->> tasks (filterv (fn [task] (not (= (:id task) op-data)))))))
    :clear (assoc old-store :tasks [])
    :update
      (update
       old-store
       :tasks
       (fn [tasks]
         (let [task-id (:id op-data), text (:text op-data)]
           (->> tasks
                (mapv (fn [task] (if (= (:id task) task-id) (assoc task :text text) task)))))))
    :hit-first (-> old-store (update-in [:tasks 0] (fn [task] (assoc task :text op-data))))
    :toggle
      (update
       old-store
       :tasks
       (fn [tasks]
         (let [task-id op-data]
           (->> tasks
                (mapv (fn [task] (if (= (:id task) task-id) (update task :done? not) task)))))))
    old-store))