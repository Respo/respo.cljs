
(ns respo.render.differ
  (:require [clojure.string :as string]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component?]]
            [clojure.set :refer [difference]]))

(declare find-element-diffs)

(declare find-children-diffs)

(defn find-style-diffs [collect! coord old-style new-style]
  (let [was-empty? (empty? old-style), now-empty? (empty? new-style)]
    (if (identical? old-style new-style)
      nil
      (cond
        (and was-empty? now-empty?) nil
        (and was-empty? (not now-empty?))
          (let [entry (first new-style), follows (rest new-style)]
            (collect! [:add-style coord entry])
            (recur collect! coord old-style follows))
        (and (not was-empty?) now-empty?)
          (let [entry (first old-style), follows (rest old-style)]
            (collect! [:rm-style coord (key entry)])
            (recur collect! coord follows new-style))
        :else
          (let [old-entry (first old-style)
                new-entry (first new-style)
                old-follows (rest old-style)
                new-follows (rest new-style)]
            (case (compare (key old-entry) (key new-entry))
              -1
                (do
                 (collect! [:rm-style coord (key old-entry)])
                 (recur collect! coord old-follows new-style))
              1
                (do
                 (collect! [:add-style coord new-entry])
                 (recur collect! coord old-style new-follows))
              (do
               (if (not (identical? (val old-entry) (val new-entry)))
                 (collect! [:replace-style coord new-entry]))
               (recur collect! coord old-follows new-follows))))))))

(defn find-props-diffs [collect! coord old-props new-props]
  (comment
   .log
   js/console
   "find props:"
   coord
   old-props
   new-props
   (count old-props)
   (count new-props))
  (let [was-empty? (empty? old-props), now-empty? (empty? new-props)]
    (cond
      (and was-empty? now-empty?) nil
      (and was-empty? (not now-empty?))
        (do
         (collect! [:add-prop coord (first new-props)])
         (recur collect! coord old-props (rest new-props)))
      (and (not was-empty?) now-empty?)
        (do
         (collect! [:rm-prop coord (key (first old-props))])
         (recur collect! coord (rest old-props) new-props))
      :else
        (let [old-entry (first old-props)
              new-entry (first new-props)
              [old-k old-v] (first old-props)
              [new-k new-v] (first new-props)
              old-follows (rest old-props)
              new-follows (rest new-props)]
          (comment .log js/console old-k new-k old-v new-v)
          (case (compare old-k new-k)
            -1
              (do
               (collect! [:rm-prop coord old-k])
               (recur collect! coord old-follows new-props))
            1
              (do
               (collect! [:add-prop coord new-entry])
               (recur collect! coord old-props new-follows))
            (do
             (if (not= old-v new-v) (collect! [:replace-prop coord new-entry]))
             (recur collect! coord old-follows new-follows)))))))

(defn find-children-diffs [collect! n-coord index old-children new-children]
  (comment .log js/console "diff children:" n-coord index old-children new-children)
  (let [was-empty? (empty? old-children), now-empty? (empty? new-children)]
    (cond
      (and was-empty? now-empty?) nil
      (and was-empty? (not now-empty?))
        (let [element (last (first new-children))]
          (collect! [:append n-coord (purify-element element)])
          (recur collect! n-coord (inc index) [] (rest new-children)))
      (and (not was-empty?) now-empty?)
        (do
         (collect! [:rm (conj n-coord index)])
         (recur collect! n-coord index (rest old-children) []))
      :else
        (let [old-keys (map first (take 32 old-children))
              new-keys (map first (take 32 new-children))
              x1 (first old-keys)
              y1 (first new-keys)
              x1-remains? (some (fn [x] (= x x1)) new-keys)
              y1-existed? (some (fn [x] (= x y1)) old-keys)
              old-follows (rest old-children)
              new-follows (rest new-children)]
          (comment println "compare:" x1 new-keys x1-remains? y1 y1-existed? old-keys)
          (cond
            (= x1 y1)
              (let [old-element (last (first old-children))
                    new-element (last (first new-children))]
                (find-element-diffs collect! (conj n-coord index) old-element new-element)
                (recur collect! n-coord (inc index) old-follows new-follows))
            (and x1-remains? (not y1-existed?))
              (do
               (collect!
                (let [element (last (first new-children))]
                  [:add (conj n-coord index) (purify-element element)]))
               (recur collect! n-coord (inc index) old-children new-follows))
            (and (not x1-remains?) y1-existed?)
              (do
               (collect! [:rm (conj n-coord index)])
               (recur collect! n-coord index old-follows new-children))
            :else
              (let [xi (.indexOf new-keys x1)
                    yi (.indexOf old-keys y1)
                    first-old-entry (first old-children)
                    first-new-entry (first new-children)]
                (comment println "index:" xi yi)
                (if (<= xi yi)
                  (let [new-element (last (first new-children))]
                    (collect! [:add (conj n-coord index) (purify-element new-element)])
                    (recur collect! n-coord (inc index) old-children new-follows))
                  (do
                   (collect! [:rm (conj n-coord index)])
                   (recur collect! n-coord index old-follows new-children)))))))))

(defn find-element-diffs [collect! n-coord old-tree new-tree]
  (comment .log js/console "element diffing:" n-coord old-tree new-tree)
  (if (identical? old-tree new-tree)
    nil
    (cond
      (component? old-tree) (recur collect! n-coord (get old-tree :tree) new-tree)
      (component? new-tree) (recur collect! n-coord old-tree (get new-tree :tree))
      :else
        (let [old-children (:children old-tree), new-children (:children new-tree)]
          (if (or (not= (:coord old-tree) (:coord new-tree))
                  (not= (:name old-tree) (:name new-tree))
                  (not= (:c-name old-tree) (:c-name new-tree)))
            (do (collect! [:replace n-coord (purify-element new-tree)]) nil)
            (do
             (find-props-diffs collect! n-coord (:attrs old-tree) (:attrs new-tree))
             (let [old-style (:style old-tree), new-style (:style new-tree)]
               (if (not (identical? old-style new-style))
                 (find-style-diffs collect! n-coord old-style new-style)))
             (let [old-events (into #{} (keys (:event old-tree)))
                   new-events (into #{} (keys (:event new-tree)))
                   added-events (difference new-events old-events)
                   removed-events (difference old-events new-events)]
               (doseq [event-name added-events]
                 (collect! [:add-event n-coord [event-name (:coord new-tree)]]))
               (doseq [event-name removed-events] (collect! [:rm-event n-coord event-name])))
             (find-children-diffs collect! n-coord 0 old-children new-children)))))))
