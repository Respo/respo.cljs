
(ns respo.util.detect )

(defn =seq [xs ys]
  (let [a-empty? (empty? xs), b-empty? (empty? ys)]
    (if (and a-empty? b-empty?)
      true
      (if (or a-empty? b-empty?)
        false
        (let [x0 (first xs), y0 (first ys)]
          (if (= (type x0) (type y0)) (if (= x0 y0) (recur (rest xs) (rest ys)) false) false))))))

(defn component? [x] (= :component (:respo-node x)))

(defn element? [x] (= :element (:respo-node x)))
