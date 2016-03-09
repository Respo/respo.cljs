
ns respo.renderer.patcher $ :require $ [] clojure.string :as string

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

defn replace-attr (target op)
  .info js/console target op
  let
    (attr-name $ name $ key op)
      attr-value $ val op
    .setAttribute target attr-name attr-value

defn add-attr (target op)
  .info js/console target op
  let
    (attr-name $ name $ key op)
      attr-value $ val op
    .setAttribute target attr-name attr-value

defn rm-attr (target op)
  .info js/console target op

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
          :replace-attr $ replace-attr target op-data
          :add-attr $ add-attr target op-data
          :rm-attr $ rm-attr target op-data
          :add-style $ add-style target op-data
          :rm-style $ rm-style target op-data
          :add $ add-element target op-data
          :rm $ rm-element target op-data
          :replace $ replace-element target op-data
          .error js/console "|not implemented:" op-type
