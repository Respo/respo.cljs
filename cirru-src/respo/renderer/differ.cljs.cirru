
ns respo.renderer.differ $ :require $ [] clojure.string :as string

declare find-element-diffs

defn sorted-rest (map-x)
  into (sorted-map)
    rest map-x

defn find-children-diffs
  acc n-coord index old-children new-children
  -- .log js/console "|diff children:" acc n-coord index old-children new-children
  cond
    (and (= 0 $ count old-children) (= 0 $ count new-children)) acc

    (and (= 0 $ count old-children) (> (count new-children) (, 0)))
      recur
        conj acc $ let
          (entry $ first new-children)
            item $ val entry
          [] :add (conj n-coord index)
            , item

        , n-coord
        inc index
        , old-children
        sorted-rest new-children

    (and (> (count old-children) (, 0)) (= 0 $ count new-children))
      recur
        conj acc $ let
          (entry $ first old-children)
            item $ val entry
          [] :rm $ conj n-coord index

        , n-coord
        inc index
        sorted-rest old-children
        , new-children

    :else $ let
      (first-old-entry $ first old-children)
        first-new-entry $ first new-children
        old-follows $ sorted-rest old-children
        new-follows $ sorted-rest new-children
      case
        compare (key first-old-entry)
          key first-new-entry
        -1 $ let
          (acc-after-cursor $ conj acc $ [] :rm $ conj n-coord index)
          recur acc-after-cursor n-coord (inc index)
            , old-follows new-children

        1 $ let
          (acc-after-cursor $ conj acc $ [] :add $ conj n-coord index)
          recur acc-after-cursor n-coord (inc index)
            , old-children new-follows

        let
          (acc-after-cursor $ find-element-diffs acc (conj n-coord index) (val first-old-entry) (val first-new-entry))

          recur acc-after-cursor n-coord (inc index)
            , old-follows new-follows

defn find-style-diffs
  acc coord old-style new-style
  cond
    (and (= 0 $ count old-style) (= 0 $ count new-style)) acc

    (and (= 0 $ count old-style) (> (count new-style) (, 0)))
      let
        (entry $ first new-style)
          follows $ sorted-rest new-style
        recur
          conj acc $ [] :add-style coord entry
          , coord old-style follows

    (and (> (count old-style) (, 0)) (= 0 $ count new-style))
      let
        (entry $ first old-style)
          follows $ sorted-rest old-style
        recur
          conj acc $ [] :rm-style coord $ key old-style
          , coord follows new-style

    :else $ let
      (old-entry $ first old-style)
        new-entry $ first new-style
        old-follows $ sorted-rest old-style
        new-follows $ sorted-rest new-style
      case
        compare (key old-entry)
          key new-entry
        -1 $ recur
          conj acc $ [] :rm-style coord $ key old-entry
          , coord old-follows new-style
        1 $ recur
          conj acc $ [] :add-style coord new-entry
          , coord old-style new-follows
        recur
          if
            = (val old-entry)
              val new-entry
            , acc
            conj acc $ [] :replace-style coord new-entry

          , coord old-follows new-follows

defn find-attr-diffs
  acc coord old-attrs new-attrs
  -- .log js/console "|find attr:" acc coord old-attrs new-attrs (count old-attrs)
    count new-attrs
  cond
    (and (= 0 $ count old-attrs) (= 0 $ count new-attrs)) acc

    (and (= 0 $ count old-attrs) (> (count new-attrs) (, 0)))
      recur
        conj acc $ [] :add-attr coord $ first new-attrs
        , coord old-attrs
        sorted-rest new-attrs

    (and (> (count old-attrs) (, 0)) (= 0 $ count new-attrs))
      recur
        conj acc $ [] :rm-attr coord $ key $ first old-attrs
        , coord
        sorted-rest old-attrs
        , new-attrs

    :else $ let
      (old-entry $ first old-attrs)
        new-entry $ first new-attrs
        ([] old-k old-v) (first old-attrs)
        ([] new-k new-v) (first new-attrs)
        old-follows $ sorted-rest old-attrs
        new-follows $ sorted-rest new-attrs

      -- .log js/console old-k new-k old-v new-v
      case (compare old-k new-k)
        -1 $ recur
          conj acc $ [] :rm-attr coord old-k
          , coord old-follows new-attrs
        1 $ recur
          conj acc $ [] :add-attr coord new-entry
          , coord old-attrs new-follows
        recur
          if (= old-v new-v)
            , acc
            if (= new-k :style)
              find-style-diffs acc coord old-v new-v
              conj acc $ [] :replace-attr coord new-entry

          , coord old-follows new-follows

defn find-element-diffs
  acc n-coord old-tree new-tree
  -- .log js/console "|element diffing:" acc n-coord old-tree new-tree
  let
    (old-coord $ :coord old-tree)
      new-coord $ :coord new-tree
      old-children $ :children old-tree
      new-children $ :children new-tree
    if (not= old-coord new-coord)
      throw $ js/Error. $ str "|coord dismatched:" old-coord new-coord
      if
        not= (:name old-tree)
          :name new-tree
        conj acc $ [] :replace n-coord new-tree
        let
          (acc-after-attrs $ find-attr-diffs acc n-coord (:attrs old-tree) (:attrs new-tree))

          -- .log js/console "|after attrs:" acc-after-attrs
          find-children-diffs acc-after-attrs n-coord 0 old-children new-children
