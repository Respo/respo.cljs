
(ns respo.render.differ
  (:require [clojure.string :as string]
            [respo.util.format :refer [purify-element]]
            [respo.util.detect :refer [component?]]
            [clojure.set :refer [difference]]))

(declare find-element-diffs)

(defn find-children-diffs [acc n-coord index old-children new-children]
  (comment
    .log
    js/console
    "diff children:"
    acc
    n-coord
    index
    old-children
    new-children)
  (let [old-size (count old-children) new-size (count new-children)]
    (cond
      (= old-size new-size 0) acc
      (and (zero? old-size) (pos? new-size)) (let 
                                               [element
                                                (last
                                                  (first new-children))
                                                next-acc
                                                (conj
                                                  acc
                                                  [:append
                                                   n-coord
                                                   (purify-element
                                                     element)])]
                                               (recur
                                                 next-acc
                                                 n-coord
                                                 (inc index)
                                                 []
                                                 (subvec
                                                   new-children
                                                   1)))
      (and (pos? old-size) (zero? new-size)) (let 
                                               [next-acc
                                                (conj
                                                  acc
                                                  [:rm
                                                   (conj
                                                     n-coord
                                                     index)])]
                                               (recur
                                                 next-acc
                                                 n-coord
                                                 index
                                                 (subvec
                                                   old-children
                                                   1)
                                                 []))
      :else (let [old-keys (mapv first old-children)
                  new-keys (mapv first new-children)
                  x1 (first old-keys)
                  y1 (first new-keys)
                  x1-remains? (some (partial = x1) new-keys)
                  y1-existed? (some (partial = y1) old-keys)
                  old-follows (subvec old-children 1)
                  new-follows (subvec new-children 1)]
              (comment
                println
                "compare:"
                x1
                new-keys
                x1-remains?
                y1
                y1-existed?
                old-keys)
              (cond
                (= x1 y1) (let [old-element (last (first old-children))
                                new-element (last (first new-children))
                                next-acc
                                (find-element-diffs
                                  acc
                                  (conj n-coord index)
                                  old-element
                                  new-element)]
                            (recur
                              next-acc
                              n-coord
                              (inc index)
                              old-follows
                              new-follows))
                (and x1-remains? (not y1-existed?)) (let 
                                                      [next-acc
                                                       (conj
                                                         acc
                                                         (let 
                                                           [element
                                                            (last
                                                              (first
                                                                new-children))]
                                                           [:add
                                                            (conj
                                                              n-coord
                                                              index)
                                                            (purify-element
                                                              element)]))]
                                                      (recur
                                                        next-acc
                                                        n-coord
                                                        (inc index)
                                                        old-children
                                                        new-follows))
                (and (not x1-remains?) y1-existed?) (let 
                                                      [next-acc
                                                       (conj
                                                         acc
                                                         [:rm
                                                          (conj
                                                            n-coord
                                                            index)])]
                                                      (recur
                                                        next-acc
                                                        n-coord
                                                        index
                                                        old-follows
                                                        new-children))
                :else (let [xi (.indexOf new-keys x1)
                            yi (.indexOf old-keys y1)
                            first-old-entry (get old-children 0)
                            first-new-entry (get new-children 0)]
                        (comment println "index:" xi yi)
                        (if (<= xi yi)
                          (let [new-element (last (first new-children))
                                next-acc
                                (conj
                                  acc
                                  [:add
                                   (conj n-coord index)
                                   (purify-element new-element)])]
                            (recur
                              next-acc
                              n-coord
                              (inc index)
                              old-children
                              new-follows))
                          (let [next-acc
                                (conj acc [:rm (conj n-coord index)])]
                            (recur
                              next-acc
                              n-coord
                              index
                              old-follows
                              new-children)))))))))

(defn find-style-diffs [acc coord old-style new-style]
  (if (identical? old-style new-style)
    acc
    (cond
      (and
        (identical? 0 (count old-style))
        (identical? 0 (count new-style))) acc
      (and (identical? 0 (count old-style)) (> (count new-style) 0)) (let 
                                                                       [entry
                                                                        (get
                                                                          new-style
                                                                          0)
                                                                        follows
                                                                        (subvec
                                                                          new-style
                                                                          1)]
                                                                       (recur
                                                                         (conj
                                                                           acc
                                                                           [:add-style
                                                                            coord
                                                                            entry])
                                                                         coord
                                                                         old-style
                                                                         follows))
      (and (> (count old-style) 0) (identical? 0 (count new-style))) (let 
                                                                       [entry
                                                                        (get
                                                                          old-style
                                                                          0)
                                                                        follows
                                                                        (subvec
                                                                          old-style
                                                                          1)]
                                                                       (recur
                                                                         (conj
                                                                           acc
                                                                           [:rm-style
                                                                            coord
                                                                            (key
                                                                              entry)])
                                                                         coord
                                                                         follows
                                                                         new-style))
      :else (let [old-entry (get old-style 0)
                  new-entry (get new-style 0)
                  old-follows (subvec old-style 1)
                  new-follows (subvec new-style 1)]
              (case
                (compare (key old-entry) (key new-entry))
                -1
                (recur
                  (conj acc [:rm-style coord (key old-entry)])
                  coord
                  old-follows
                  new-style)
                1
                (recur
                  (conj acc [:add-style coord new-entry])
                  coord
                  old-style
                  new-follows)
                (recur
                  (if (identical? (val old-entry) (val new-entry))
                    acc
                    (conj acc [:replace-style coord new-entry]))
                  coord
                  old-follows
                  new-follows))))))

(defn find-props-diffs [acc coord old-props new-props]
  (comment
    .log
    js/console
    "find props:"
    acc
    coord
    old-props
    new-props
    (count old-props)
    (count new-props))
  (cond
    (and (= 0 (count old-props)) (= 0 (count new-props))) acc
    (and (= 0 (count old-props)) (> (count new-props) 0)) (recur
                                                            (conj
                                                              acc
                                                              [:add-prop
                                                               coord
                                                               (get
                                                                 new-props
                                                                 0)])
                                                            coord
                                                            old-props
                                                            (subvec
                                                              new-props
                                                              1))
    (and (> (count old-props) 0) (= 0 (count new-props))) (recur
                                                            (conj
                                                              acc
                                                              [:rm-prop
                                                               coord
                                                               (key
                                                                 (get
                                                                   old-props
                                                                   0))])
                                                            coord
                                                            (subvec
                                                              old-props
                                                              1)
                                                            new-props)
    :else (let [old-entry (get old-props 0)
                new-entry (get new-props 0)
                [old-k old-v] (get old-props 0)
                [new-k new-v] (get new-props 0)
                old-follows (subvec old-props 1)
                new-follows (subvec new-props 1)]
            (comment .log js/console old-k new-k old-v new-v)
            (case
              (compare old-k new-k)
              -1
              (recur
                (conj acc [:rm-prop coord old-k])
                coord
                old-follows
                new-props)
              1
              (recur
                (conj acc [:add-prop coord new-entry])
                coord
                old-props
                new-follows)
              (recur
                (if (= old-v new-v)
                  acc
                  (conj acc [:replace-prop coord new-entry]))
                coord
                old-follows
                new-follows)))))

(defn find-element-diffs [acc n-coord old-tree new-tree]
  (comment
    .log
    js/console
    "element diffing:"
    acc
    n-coord
    old-tree
    new-tree)
  (if (identical? old-tree new-tree)
    acc
    (cond
      (component? old-tree) (recur
                              acc
                              n-coord
                              (get old-tree :tree)
                              new-tree)
      (component? new-tree) (recur
                              acc
                              n-coord
                              old-tree
                              (get new-tree :tree))
      :else (let [old-children (:children old-tree)
                  new-children (:children new-tree)]
              (if (or
                    (not= (:coord old-tree) (:coord new-tree))
                    (not= (:name old-tree) (:name new-tree))
                    (not= (:c-name old-tree) (:c-name new-tree)))
                (conj acc [:replace n-coord (purify-element new-tree)])
                (-> acc
                 ((fn [acc1]
                    (let [old-style (:style old-tree)
                          new-style (:style new-tree)]
                      (if (identical? old-style new-style)
                        acc1
                        (find-style-diffs
                          acc1
                          n-coord
                          old-style
                          new-style)))))
                 (find-props-diffs
                   n-coord
                   (:attrs old-tree)
                   (:attrs new-tree))
                 ((fn [acc1]
                    (let [old-events (into
                                       #{}
                                       (keys (:event old-tree)))
                          new-events (into
                                       #{}
                                       (keys (:event new-tree)))
                          added-events (difference
                                         new-events
                                         old-events)
                          removed-events (difference
                                           old-events
                                           new-events)
                          changes (concat
                                    (map
                                      (fn 
                                        [event-name]
                                        [:add-event
                                         n-coord
                                         event-name])
                                      added-events)
                                    (map
                                      (fn 
                                        [event-name]
                                        [:rm-event n-coord event-name])
                                      removed-events))]
                      (if (empty? changes)
                        acc1
                        (into [] (concat acc1 changes))))))
                 (find-children-diffs
                   n-coord
                   0
                   old-children
                   new-children)))))))
