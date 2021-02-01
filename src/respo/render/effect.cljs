
(ns respo.render.effect
  (:require [respo.schema.op :as op]
            [respo.util.detect :refer [component? element? =seq]]
            [respo.util.list :refer [val-of-first]]))

(defn collect-mounting [collect! coord n-coord tree at-place?]
  (cond
    (component? tree)
      (let [effects (:effects tree), new-coord (conj coord (:name tree))]
        (when-not (empty? effects)
          (doseq [effect effects]
            (let [method (:method effect)]
              (collect!
               [op/effect-mount
                new-coord
                n-coord
                (fn [target] (method (:args effect) [:mount target at-place?]))]))))
        (recur collect! new-coord n-coord (:tree tree) false))
    (element? tree)
      (loop [children (:children tree), idx 0]
        (when-not (empty? children)
          (collect-mounting
           collect!
           (conj coord (first (first children)))
           (conj n-coord idx)
           (val-of-first children)
           false)
          (recur (rest children) (inc idx))))
    :else (js/console.warn "Unknown entry for mounting:" tree)))

(defn collect-unmounting [collect! coord n-coord tree at-place?]
  (cond
    (component? tree)
      (let [effects (:effects tree), new-coord (conj coord (:name tree))]
        (collect-unmounting collect! new-coord n-coord (:tree tree) false)
        (when-not (empty? effects)
          (doseq [effect effects]
            (let [method (:method effect)]
              (collect!
               [op/effect-unmount
                new-coord
                n-coord
                (fn [target] (method (:args effect) [:unmount target at-place?]))])))))
    (element? tree)
      (loop [children (:children tree), idx 0]
        (when-not (empty? children)
          (collect-unmounting
           collect!
           (conj coord (first (first children)))
           (conj n-coord idx)
           (val-of-first children)
           false)
          (recur (rest children) (inc idx))))
    :else (js/console.warn "Unknown entry for unmounting:" tree)))

(defn collect-updating [collect! action coord n-coord old-tree new-tree]
  (assert (component? new-tree) "Expected component for updating")
  (let [effects (:effects new-tree)
        old-effects (:effects old-tree)
        new-coord (conj coord (:name new-tree))]
    (when-not (empty? effects)
      (comment js/console.log "collect update" n-coord (:effects new-tree))
      (loop [new-items effects, old-items old-effects]
        (let [new-effect (first new-items)
              old-effect (first old-items)
              method (:method new-effect)]
          (when-not (and (= (:name new-effect) (:name old-effect))
                         (=seq (:args new-effect) (:args old-effect)))
            (collect!
             [(if (= :update action) op/effect-update op/effect-before-update)
              new-coord
              n-coord
              (fn [target] (method (:args new-effect) [action target]))])))
        (when-not (empty? new-items) (recur (rest new-items) (rest old-items)))))))
