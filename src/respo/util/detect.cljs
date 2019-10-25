
(ns respo.util.detect )

(defn =seq [xs ys]
  (let [a-empty? (empty? xs), b-empty? (empty? ys)]
    (cond
      (and a-empty? b-empty?) true
      (or a-empty? b-empty?) false
      (= (first xs) (first ys)) (recur (rest xs) (rest ys))
      :else false)))

(defn component? [x] (= :component (:respo-node x)))

(defn effect? [x] (= :effect (:respo-node x)))

(defn element? [x] (= :element (:respo-node x)))
