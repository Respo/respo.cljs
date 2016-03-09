
ns respo.renderer.patcher $ :require
  [] clojure.string :as string
  [] respo.util.format :refer $ [] dashed->camel

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
  .info js/console target op

defn rm-style (target op)
  .info js/console target op

defn replace-style (target op)
  .info js/console target op

defn add-element (target op)
  .info js/console target op

defn rm-element (target op)
  .info js/console target op

defn replace-element (target op)
  .info js/console target op

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
          .error js/console "|not implemented:" op-type
