
(ns respo.util.detect
  (:import [respo.alias Component Element]))

(defn component? [x] (contains? x :tree))

(defn element? [x] (contains? x :event))

(defn =vector [a b] false)
