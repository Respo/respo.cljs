
ns respo.renderer.differ $ :require $ [] clojure.string :as string

defn find-children-diffs (acc old-children new-children)
  cond
    (and (= 0 $ count old-children) (= 0 $ count new-children)) acc

    (and (= 0 $ count old-children) (> (count new-children) (, 0)))
      recur
        conj acc
          let $
            entry $ first new-children
            item $ val entry
          [] :add (:coord item)
            , item

        , old-children
        into (sorted-map)
          rest new-children

    (and (> (count old-children) (, 0)) (= 0 $ count new-children))
      recur
        conj acc $ let
          (entry $ first old-children)
            item $ val entry
          [] :rm $ :coord item

        into (sorted-map)
          rest old-children
        , new-children

    :else $ let
      (first-old-entry $ first old-children)
        first-new-entry $ first new-children
        old-follows $ into (sorted-map)
          rest old-children
        new-follows $ into (sorted-map)
          rest new-children

      case
        compare (key first-old-entry)
          key first-new-entry
        -1 $ let
          (acc-after-cursor $ conj acc $ [] :rm $ :coord $ val first-old-entry)
          recur acc-after-cursor old-follows new-children

        1 $ let
          (acc-after-cursor $ conj acc $ [] :add $ :coord $ val first-old-entry)
          recur acc-after-cursor old-children new-follows

        let
          (acc-after-cursor $ find-element-diffs acc (val first-old-entry) (val first-new-entry))

          recur acc-after-cursor old-follows new-follows

defn find-style-diffs
  acc coord old-style new-style
  cond
    (and (= 0 $ count old-style) (= 0 $ count new-style)) acc

    (and (= 0 $ count old-style) (> (count new-style) (, 0)))
      let
        (entry $ first new-style)
          follows $ into (sorted-map)
            rest new-style

        conj acc $ [] :add-style coord entry
        , old-style follows

    (and (> (count old-style) (, 0)) (= 0 $ count new-style))
      let
        (entry $ first old-style)
          follows $ into (sorted-map)
            rest old-styles

        conj acc $ [] :rm-style coord $ key old-style
        , follows new-style

    :else $ let
      (old-entry $ first old-style)
        new-entry $ first new-style
        old-follows $ into (sorted-map)
          rest old-style
        new-follows $ into (sorted-map)
          rest new-style

      case
        compare (key old-entry)
          key new-entry
        -1 $ recur
          conj acc $ [] :rm-style coord $ key old-entry
          , old-follows new-style
        1 $ recur
          conj acc $ [] :add-style coord new-entry
          , old-style new-follows
        if
          = (val old-entry)
            val new-entry
          , acc
          conj acc $ [] :replace-style coord new-entry

defn find-attrs-diffs
  acc coord old-attrs new-attrs
  cond
    (and (= 0 $ count old-attrs) (= 0 $ count new-attrs)) acc

    (and (= 0 $ count old-attrs) (> (count new-attrs) (, 0)))
      recur
        conj acc $ [] :add-attr coord $ first new-attrs
        , old-attrs
        into (sorted-map)
          rest new-attrs

    (and (> (count old-attrs) (, 0)) (= 0 $ count new-attrs))
      recur
        conj acc $ [] :rm-attr coord $ key $ first old-attrs
        into (sorted-map)
          rest old-attrs
        , new-attrs

    :else $ let
      (old-entry $ first old-attrs)
        new-entry $ first new-attrs
        ([] old-k old-v) (first old-attrs)
        ([] new-k new-v) (first new-attrs)
        old-follows $ into (sorted-map)
          rest old-attrs
        new-follows $ into (sorted-map)
          rest new-attrs

      case (compare old-k new-k)
        -1 $ recur
          conj acc $ [] :rm-attr coord old-k
          , old-follows new-attrs
        1 $ recur
          conj acc $ [] :add-attr coord new-entry
          , old-attrs new-follows
        if (= old-v new-v)
          , acc
          recur
            if (= new-k :style)
              find-style-diffs coord old-v new-v
              conj acc $ [] :replace-attr coord new-entry
            , old-follows new-follows

defn find-element-diffs (acc old-tree new-tree)
  let
    (old-coord $ :coord old-tree)
      new-coord $ :coord new-tree
    if (not= old-coord new-coord)
      throw $ Exception. $ str "|coord dismatched:" old-coord new-coord
      if
        not= (:name old-tree)
          :name new-tree
        conj acc ([] :rm new-coord)
          [] :add new-coord new-tree
        let
          (acc-after-attrs $ find-attr-diffs acc new-coord (:attrs old-tree) (:attrs new-tree))

          , find-children-diffs acc-after-attrs old-children new-children
