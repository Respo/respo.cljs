
(ns respo.util.list)

(defn filter-first [f xs] (reduce (fn [acc x] (when (f x) (reduced x))) nil xs))
