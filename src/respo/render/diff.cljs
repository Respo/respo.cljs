
(ns respo.render.diff
  (:require [clojure.string :as string]
            [respo.util.format :refer []]
            [respo.util.detect :refer [component? element?]]
            [clojure.set :refer [difference]]
            [respo.schema.op :as op]
            [respo.util.comparator :refer [compare-xy]]
            [respo.render.effect
             :refer
             [collect-mounting collect-updating collect-unmounting]]
            [respo.util.list :refer [val-of-first]]))

(declare find-children-diffs)

(declare find-element-diffs)

(defn find-props-diffs [collect! coord n-coord old-props new-props]
  (comment
   .log
   js/console
   "find props:"
   n-coord
   old-props
   new-props
   (count old-props)
   (count new-props))
  (let [was-empty? (empty? old-props), now-empty? (empty? new-props)]
    (cond
      (and was-empty? now-empty?) nil
      (and was-empty? (not now-empty?))
        (do
         (collect! [op/add-prop coord n-coord (first new-props)])
         (recur collect! coord n-coord old-props (rest new-props)))
      (and (not was-empty?) now-empty?)
        (do
         (collect! [op/rm-prop coord n-coord (key (first old-props))])
         (recur collect! coord n-coord (rest old-props) new-props))
      :else
        (let [old-entry (first old-props)
              new-entry (first new-props)
              [old-k old-v] (first old-props)
              [new-k new-v] (first new-props)
              old-follows (rest old-props)
              new-follows (rest new-props)]
          (comment .log js/console old-k new-k old-v new-v)
          (case (compare-xy old-k new-k)
            -1
              (do
               (collect! [op/rm-prop coord n-coord old-k])
               (recur collect! coord n-coord old-follows new-props))
            1
              (do
               (collect! [op/add-prop coord n-coord new-entry])
               (recur collect! coord n-coord old-props new-follows))
            (do
             (if (not= old-v new-v) (collect! [op/replace-prop coord n-coord new-entry]))
             (recur collect! coord n-coord old-follows new-follows)))))))

(defn find-style-diffs [collect! coord n-coord old-style new-style]
  (let [was-empty? (empty? old-style), now-empty? (empty? new-style)]
    (if (identical? old-style new-style)
      nil
      (cond
        (and was-empty? now-empty?) nil
        (and was-empty? (not now-empty?))
          (let [entry (first new-style), follows (rest new-style)]
            (collect! [op/add-style coord n-coord entry])
            (recur collect! coord n-coord old-style follows))
        (and (not was-empty?) now-empty?)
          (let [entry (first old-style), follows (rest old-style)]
            (collect! [op/rm-style coord n-coord (key entry)])
            (recur collect! coord n-coord follows new-style))
        :else
          (let [old-entry (first old-style)
                new-entry (first new-style)
                old-follows (rest old-style)
                new-follows (rest new-style)]
            (case (compare-xy (key old-entry) (key new-entry))
              -1
                (do
                 (collect! [op/rm-style coord n-coord (key old-entry)])
                 (recur collect! coord n-coord old-follows new-style))
              1
                (do
                 (collect! [op/add-style coord n-coord new-entry])
                 (recur collect! coord n-coord old-style new-follows))
              (do
               (if (not (identical? (val old-entry) (val new-entry)))
                 (collect! [op/replace-style coord n-coord new-entry]))
               (recur collect! coord n-coord old-follows new-follows))))))))

(defn find-element-diffs [collect! coord n-coord old-tree new-tree]
  (comment .log js/console "element diffing:" n-coord old-tree new-tree)
  (cond
    (identical? old-tree new-tree) nil
    (and (component? old-tree) (component? new-tree))
      (let [new-coord (conj coord (:name new-tree))]
        (if (= (:name old-tree) (:name new-tree))
          (do
           (collect-updating collect! :before-update coord n-coord old-tree new-tree)
           (find-element-diffs collect! new-coord n-coord (:tree old-tree) (:tree new-tree))
           (collect-updating collect! :update coord n-coord old-tree new-tree))
          (do
           (collect-unmounting collect! coord n-coord old-tree true)
           (find-element-diffs collect! new-coord n-coord (:tree old-tree) (:tree new-tree))
           (collect-mounting collect! coord n-coord new-tree true))))
    (and (component? old-tree) (element? new-tree))
      (do
       (collect-unmounting collect! coord n-coord old-tree true)
       (recur collect! coord n-coord (:tree old-tree) new-tree))
    (and (element? old-tree) (component? new-tree))
      (let [new-coord (conj coord (:name new-tree))]
        (find-element-diffs collect! new-coord coord n-coord old-tree (:tree new-tree))
        (collect-mounting collect! coord n-coord new-tree true))
    (and (element? old-tree) (element? new-tree))
      (let [old-children (:children old-tree), new-children (:children new-tree)]
        (if (not= (:name old-tree) (:name new-tree))
          (do (collect! [op/replace-element coord n-coord new-tree]) nil)
          (do
           (find-props-diffs collect! coord n-coord (:attrs old-tree) (:attrs new-tree))
           (let [old-style (:style old-tree), new-style (:style new-tree)]
             (if (not= old-style new-style)
               (find-style-diffs collect! coord n-coord old-style new-style)))
           (let [old-events (into #{} (keys (:event old-tree)))
                 new-events (into #{} (keys (:event new-tree)))
                 added-events (difference new-events old-events)
                 removed-events (difference old-events new-events)]
             (doseq [event-name added-events]
               (collect! [op/set-event coord n-coord event-name]))
             (doseq [event-name removed-events]
               (collect! [op/rm-event coord n-coord event-name])))
           (find-children-diffs collect! coord n-coord 0 old-children new-children))))
    :else (js/console.warn "Diffing unknown params" old-tree new-tree)))

(defn find-children-diffs [collect! coord n-coord index old-children new-children]
  (comment .log js/console "diff children:" n-coord index old-children new-children)
  (let [was-empty? (empty? old-children), now-empty? (empty? new-children)]
    (cond
      (and was-empty? now-empty?) nil
      (and was-empty? (not now-empty?))
        (let [pair (first new-children)
              k (first pair)
              element (last pair)
              new-coord (conj coord k)]
          (collect! [op/append-element new-coord n-coord element])
          (collect-mounting collect! new-coord (conj n-coord index) element true)
          (recur collect! coord n-coord (inc index) [] (rest new-children)))
      (and (not was-empty?) now-empty?)
        (let [pair (first old-children)
              old-k (first pair)
              old-tree (last pair)
              next-n-coord (conj n-coord index)
              old-coord (conj coord old-k)]
          (do
           (collect-unmounting collect! old-coord next-n-coord old-tree true)
           (collect! [op/rm-element old-coord (conj n-coord index) nil])
           (recur collect! coord n-coord index (rest old-children) [])))
      :else
        (let [old-keys (map first (take 16 old-children))
              new-keys (map first (take 16 new-children))
              x1 (first old-keys)
              y1 (first new-keys)
              match-x1 (fn [x] (= x x1))
              match-y1 (fn [x] (= x y1))
              x1-remains? (some match-x1 new-keys)
              y1-existed? (some match-y1 old-keys)
              old-follows (rest old-children)
              new-follows (rest new-children)]
          (comment println "compare:" x1 new-keys x1-remains? y1 y1-existed? old-keys)
          (cond
            (= x1 y1)
              (let [old-element (val-of-first old-children)
                    new-element (val-of-first new-children)]
                (find-element-diffs
                 collect!
                 (conj coord x1)
                 (conj n-coord index)
                 old-element
                 new-element)
                (recur collect! coord n-coord (inc index) old-follows new-follows))
            (and x1-remains? (not y1-existed?))
              (let [next-coord (conj coord y1), next-n-coord (conj n-coord index)]
                (collect!
                 (let [element (val-of-first new-children)]
                   [op/add-element next-coord next-n-coord element]))
                (collect-mounting
                 collect!
                 next-coord
                 next-n-coord
                 (val-of-first new-children)
                 true)
                (recur collect! coord n-coord (inc index) old-children new-follows))
            (and (not x1-remains?) y1-existed?)
              (let [next-coord (conj coord x1), next-n-coord (conj n-coord index)]
                (collect-unmounting
                 collect!
                 coord
                 next-n-coord
                 (val-of-first old-children)
                 true)
                (collect! [op/rm-element next-coord next-n-coord nil])
                (recur collect! coord n-coord index old-follows new-children))
            :else
              (let [xi (.indexOf new-keys x1)
                    yi (.indexOf old-keys y1)
                    first-old-entry (first old-children)
                    first-new-entry (first new-children)
                    next-coord (conj coord y1)
                    next-n-coord (conj n-coord index)]
                (comment println "index:" xi yi)
                (if (<= xi yi)
                  (let [new-element (val-of-first new-children)]
                    (collect! [op/add-element next-coord next-n-coord new-element])
                    (collect-mounting
                     collect!
                     next-coord
                     next-n-coord
                     (val-of-first new-children)
                     true)
                    (recur collect! coord n-coord (inc index) old-children new-follows))
                  (do
                   (collect-unmounting
                    collect!
                    next-coord
                    next-n-coord
                    (val-of-first old-children)
                    true)
                   (collect! [op/rm-element next-coord next-n-coord nil])
                   (recur collect! coord n-coord index old-follows new-children)))))))))
