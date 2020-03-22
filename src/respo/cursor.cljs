
(ns respo.cursor )

(defn updater-states [store [cursor new-state]]
  (assoc-in store (concat [:states] cursor [:data]) new-state))
