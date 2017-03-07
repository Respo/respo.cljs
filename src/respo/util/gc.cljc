
(ns respo.util.gc
  (:require [respo.util.detect :refer [component?]]
            [respo.controller.resolver :refer [get-component-at]]))

(defn find-removed [state-tree element base]
  (->> state-tree
       (map
        (fn [entry]
          (let [[coord-key sub-tree] entry]
            (if (= coord-key 'data)
              []
              (let [this-path (conj base coord-key)
                    maybe-component (get-component-at element this-path)]
                (if (some? maybe-component)
                  (find-removed sub-tree element this-path)
                  [this-path]))))))
       (apply concat)))

(defn remove-by-path [tree path]
  (if (empty? path)
    tree
    (let [init-path (butlast path), pos (last path)]
      (if (empty? init-path)
        (dissoc tree pos)
        (update-in tree init-path (fn [cursor] (dissoc cursor pos)))))))

(defn apply-remove [state-tree removed-paths]
  (if (empty? removed-paths)
    state-tree
    (let [path (first removed-paths), next-state (remove-by-path state-tree path)]
      (recur next-state (rest removed-paths)))))
