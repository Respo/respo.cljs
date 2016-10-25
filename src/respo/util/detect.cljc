
(ns respo.util.detect )

(defn element? [x] (contains? x :event))

(defn component? [x] (contains? x :tree))

(defn =vector [a b]
  (if (not= (count a) (count b))
    false
    (loop [ax a, bx b]
      (if (identical? (count ax) 0)
        true
        (if (identical? (get ax 0) (get bx 0)) (recur (subvec ax 1) (subvec bx 1)) false)))))
