
(ns respo.cursor )

(defn mutate [op-data]
  (fn [states]
    (let [[cursor next-state] op-data] (assoc-in states (conj cursor :data) next-state))))
