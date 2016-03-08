
ns respo.controller.resolver $ :require $ [] clojure.string :as string

defn vector-contains? (vec-a vec-b)
  cond
    (and (= (count vec-a) (, 0)) (= (count vec-b) (, 0))) true

    (and (= (count vec-a) (, 0)) (> (count vec-b) (, 0))) false

    (and (> (count vec-a) (, 0)) (= (count vec-b) (, 0))) true

    :else $ recur (rest vec-a)
      rest vec-b

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
    if
      and (some? target-element)
        contains? (:events target-element)
          , event-name

      , target-element
      if
        = coord $ []
        , nil
        recur element
          subvec coord 0 $ - (count coord)
            , 1
          , event-name
