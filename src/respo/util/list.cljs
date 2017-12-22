
(ns respo.util.list (:require [respo.util.detect :refer [component? element?]]))

(defn filter-first [f xs] (reduce (fn [acc x] (when (f x) (reduced x))) nil xs))

(defn pick-attrs [props]
  (if (nil? props)
    (list)
    (->> (-> props (dissoc :on) (dissoc :event) (dissoc :style))
         (filter (fn [[k v]] (not (re-matches (re-pattern "on-\\w+") (name k)))))
         (sort-by first))))

(defn val-exists? [pair] (some? (last pair)))

(defn arrange-children [children] (->> (map-indexed vector children) (filter val-exists?)))

(defn pick-event [props]
  (merge
   (:on props)
   (->> props
        (filter (fn [[k v]] (re-matches (re-pattern "on-\\w+") (name k))))
        (map (fn [[k v]] [(keyword (subs (name k) 3)) v]))
        (into {}))))
