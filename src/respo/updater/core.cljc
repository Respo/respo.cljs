
(ns respo.updater.core
  (:require [clojure.string :as string]))

(defn updater [old-store op-type op-data op-id]
  (comment
    println
    (pr-str old-store)
    (pr-str op-type)
    (pr-str op-data))
  (case
    op-type
    :add
    (conj old-store {:done? false, :id op-id, :text op-data})
    :remove
    (->> old-store (filterv (fn [task] (not (= (:id task) op-data)))))
    :clear
    []
    :update
    (let [task-id (:id op-data) text (:text op-data)]
      (->>
        old-store
        (mapv
          (fn [task]
            (if (= (:id task) task-id)
              (assoc task :text text)
              task)))))
    :toggle
    (let [task-id op-data]
      (->>
        old-store
        (mapv
          (fn [task]
            (if (= (:id task) task-id)
              (update task :done? not)
              task)))))
    old-store))
