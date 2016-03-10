
ns respo.renderer.patcher $ :require
  [] clojure.string :as string
  [] respo.util.format :refer $ [] dashed->camel
  [] respo.renderer.make-dom :refer $ [] make-element

defn find-target (root coord)
  if
    = coord $ []
    , root
    let
      (index $ first coord)
        follows $ subvec coord 1
        child $ aget (.-children root)
          , index

      recur child follows

defn replace-prop (target op)
  let
    (prop-name $ dashed->camel $ name $ key op)
      prop-value $ val op
    aset target prop-name prop-value

defn add-prop (target op)
  let
    (prop-name $ dashed->camel $ name $ key op)
      prop-value $ val op
    aset target prop-name prop-value

defn rm-prop (target op)
  js-delete target $ dashed->camel $ name op

defn add-style (target op)
  let
    (style-name $ dashed->camel $ name $ key op)
      style-value $ val op
    aset (.-style target)
      , style-name style-value

defn rm-style (target op)
  let
    (style-name $ dashed->camel $ name op)
    js/delete (.-style target)
      , style-name

defn replace-style (target op)
  let
    (style-name $ dashed->camel $ name $ key op)
      style-value $ val op
    aset (.-style target)
      , style-name style-value

defn add-element (target op)
  let
    (new-element $ make-element op)
      parent-element $ .-parentElement target
      next-element $ .-nextElementSibling target
    if (some? next-element)
      .insertBefore next-element new-element
      .appendChild parent-element new-element

defn rm-element (target op)
  .remove target

defn replace-element (target op)
  let
    (new-element $ make-element op)
    .insertBefore target new-element
    .remove target

defn append-element (target op)
  let
    (new-element $ make-element op)
    .appendChild target new-element

defn apply-dom-changes (changes mount-point)
  let
    (root $ .-firstChild mount-point)
    doall $ ->> changes $ map $ fn (op)
      let
        (op-type $ first op)
          coord $ get op 1
          op-data $ get op 2
          target $ find-target root coord
        .log js/console op-type target op-data
        case op-type
          :replace-prop $ replace-prop target op-data
          :add-prop $ add-prop target op-data
          :rm-prop $ rm-prop target op-data
          :add-style $ add-style target op-data
          :rm-style $ rm-style target op-data
          :add $ add-element target op-data
          :rm $ rm-element target op-data
          :replace $ replace-element target op-data
          :append $ append-element target op-data
          .error js/console "|not implemented:" op-type
