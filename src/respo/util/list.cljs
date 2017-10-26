
(ns respo.util.list (:require [respo.util.detect :refer [component? element? dsl?]]))

(defn filter-first [f xs] (reduce (fn [acc x] (when (f x) (reduced x))) nil xs))

(defn pick-attrs [props]
  (if (nil? props)
    (list)
    (let [base-attrs (merge (-> props (dissoc :on) (dissoc :event) (dissoc :style)))]
      (sort-by first base-attrs))))

(defn val-exists? [pair] (some? (last pair)))

(defn arrange-children [children] (->> (map-indexed vector children) (filter val-exists?)))

(defn arrange-children-old [children]
  (->> (if (and (= 1 (count children))
                (not (component? (first children)))
                (not (element? (first children)))
                (not (dsl? (first children))))
         (first children)
         (map-indexed vector children))
       (filter val-exists?)))
