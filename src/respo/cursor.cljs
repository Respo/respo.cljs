
(ns respo.cursor )

(defn update-states [store [cursor new-state]]
  (assoc-in store (concat [:states] cursor [:data]) new-state))
