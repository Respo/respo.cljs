
(ns respo.util.detect )

(defn element? [x] (and (map? x) (contains? x :attrs) (contains? x :style)))

(defn =seq [a b]
  (let [a-empty? (empty? a), b-empty? (empty? b)]
    (if (and a-empty? b-empty?)
      true
      (if (or a-empty? b-empty?)
        false
        (if (identical? (first a) (first b)) (recur (rest a) (rest b)) false)))))

(defn component? [x] (and (map? x) (contains? x :tree) (contains? x :render)))
