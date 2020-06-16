
(ns respo.util.list
  (:require [respo.util.detect :refer [component? element?]]
            [respo.util.comparator :refer [compare-xy]]))

(defn detect-func-in-map? [params]
  (if (empty? params)
    false
    (let [p0 (first params)]
      (if (and (map? p0) (some (fn [[k v]] (fn? v)) p0)) true (recur (rest params))))))

(defn filter-first [f xs] (reduce (fn [acc x] (when (f x) (reduced x))) nil xs))

(defn map-val [f xs]
  (assert (fn? f) "expects f to be a function")
  (assert (or (map? xs) (sequential? xs) (nil? xs)) "expects xs to be a collection")
  (map (fn [[k v]] [k (f v)]) xs))

(defn map-with-idx [f xs]
  (assert (fn? f) "expects function")
  (assert (sequential? xs) "expects sequence")
  (map-indexed (fn [idx x] [idx (f x)]) xs))

(defn pick-attrs [props]
  (if (nil? props)
    (list)
    (->> (-> props (dissoc :on) (dissoc :event) (dissoc :style))
         (filter (fn [[k v]] (not (re-matches (re-pattern "on-\\w+") (name k)))))
         (sort (fn [x y] (compare-xy (first x) (first y)))))))

(defn pick-event [props]
  (merge
   (:on props)
   (->> props
        (filter (fn [[k v]] (re-matches (re-pattern "on-\\w+") (name k))))
        (map (fn [[k v]] [(keyword (subs (name k) 3)) v]))
        (into {}))))

(defn val-exists? [pair] (some? (last pair)))

(defn val-of-first [x] (last (first x)))
