
ns respo.controller.resolver $ :require $ [] clojure.string :as string

defn get-element-at (element coord)
  if
    = coord $ []
    , element
    let
      (coord-first $ first coord)
        coord-rest $ subvec coord 1
        child $ get-in element $ [] :children coord-first
      if (some? child)
        get-element-at child coord-rest
        throw $ js/Error. "|child not found"

defn find-event-target (element coord event-name)
  -- .log js/console |targeting: element coord event-name
  let
    (target-element $ get-element-at element coord)
      element-exists? $ some? target-element
    if
      and element-exists? $ contains? (:events target-element)
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
