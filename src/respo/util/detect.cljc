
(ns respo.util.detect )

(defn element? [x] (contains? x :event))

(defn component? [x] (contains? x :tree))

(defn =seq [a b]
  (let [a-empty? (empty? a), b-empty? (empty? b)]
    (if (and a-empty? b-empty?)
      true
      (if (or a-empty? b-empty?)
        false
        (if (identical? (first a) (first b)) (recur (rest a) (rest b)) false)))))
