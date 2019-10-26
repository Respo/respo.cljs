
(ns respo.render.effect
  (:require [respo.schema.op :as op] [respo.util.detect :refer [component? element? =seq]]))

(defn collect-mounting [collect! n-coord tree]
  (cond
    (component? tree)
      (let [effects (:effects tree)]
        (when-not (empty? effects)
          (doseq [effect effects]
            (let [method (:method effect)]
              (collect!
               [op/run-effect n-coord (fn [target] (method (:args effect) [:mount target]))]))))
        (recur collect! n-coord (:tree tree)))
    (element? tree)
      (loop [children (:children tree), idx 0]
        (when-not (empty? children)
          (collect-mounting collect! (conj n-coord idx) (last (first children)))
          (recur (rest children) (inc idx))))
    :else (js/console.warn "Unknown entry for mounting:" tree)))

(defn collect-unmounting [collect! n-coord tree]
  (cond
    (component? tree)
      (let [effects (:effects tree)]
        (collect-unmounting collect! n-coord (:tree tree))
        (when-not (empty? effects)
          (doseq [effect effects]
            (let [method (:method effect)]
              (collect!
               [op/run-effect
                n-coord
                (fn [target] (method (:args effect) [:unmount target]))])))))
    (element? tree)
      (loop [children (:children tree), idx 0]
        (when-not (empty? children)
          (collect-unmounting collect! (conj n-coord idx) (last (first children)))
          (recur (rest children) (inc idx))))
    :else (js/console.warn "Unknown entry for unmounting:" tree)))

(defn collect-updating [collect! n-coord old-tree new-tree]
  (let [effects (:effects new-tree)]
    (when (not (empty? effects))
      (comment js/console.log "collect update" n-coord (:effects new-tree))
      (doseq [idx (range (count effects))]
        (let [old-effect (get-in old-tree [:effects idx])
              new-effect (get effects idx)
              method (:method new-effect)]
          (comment println old-effect new-effect)
          (when-not (=seq (:args new-effect) (:args old-effect))
            (collect!
             [op/run-effect
              n-coord
              (fn [target] (method (:args new-effect) [:update target]))])))))))
