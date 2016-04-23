
ns respo.controller.resolver $ :require
  [] clojure.string :as string
  [] respo.util.format :refer $ [] purify-element
  [] respo.alias :refer $ [] Element Component

defn get-markup-at (markup coord)
  -- .log js/console "|get markup:" $ pr-str coord
  if
    = coord $ []
    , markup
    if
      = Component $ type markup
      recur (:tree markup)
        subvec coord 1
      let
        (coord-first $ first coord)
          child $ get-in markup ([] :children coord-first)

        if (some? child)
          recur child $ subvec coord 1
          throw $ js/Error.
            str "|child not found:" coord $ purify-element markup

defn find-event-target (element coord event-name)
  let
    (target-element $ get-markup-at element coord)
      element-exists? $ some? target-element
    -- .log js/console "|target element:" $ pr-str (:c-coord target-element)
      , event-name
    if
      and element-exists? $ contains? (:event target-element)
        , event-name
      , target-element
      if
        = coord $ []
        , nil
        if element-exists?
          recur element
            subvec coord 0 $ - (count coord)
              , 1
            , event-name

          , nil
