
ns respo.controller.deliver $ :require
  respo.controller.resolver :refer $ [] find-event-target get-markup-at

defn all-component-coords (element)
  conj
    map
      fn (child-entry)
        all-component-coords $ val child-entry
      :children element

    :component-coord element

defn purify-states (new-states old-states all-coords)
  if
    = (count old-states)
      , 0
    , new-states
    let
      (first-entry $ first old-states)
      recur
        if
          some
            fn (component-coord)
              = component-coord $ key first-entry
            , all-coords

          assoc new-states (key first-entry)
            val first-entry
          , new-states

        rest old-states
        , all-coords

defn do-states-gc (states-ref element)
  println "|states GC:" $ pr-str @states-ref
  let
    (all-coords $ distinct (all-component-coords element))
      new-states $ purify-states ({})
        , @states-ref all-coords

    reset! states-ref new-states

defn build-deliver-event (element-ref dispatch states-ref)
  fn (coord event-name simple-event)
    let
      (target-element $ find-event-target @element-ref coord event-name)
        target-listener $ get (:event target-element)
          , event-name
        c-coord $ :c-coord target-element
        comp-element $ get-markup-at @element-ref c-coord
        init-state $ :init-state comp-element
        update-state $ :update-state comp-element
        state $ if (contains? @states-ref c-coord)
          get @states-ref c-coord
          apply init-state $ :args comp-element
        mutate $ fn (& args)
          let
            (new-state $ apply update-state (cons state args))

            swap! states-ref assoc c-coord new-state

      if (some? target-listener)
        do
          println "|listener found:" coord event-name
          target-listener simple-event dispatch mutate
        println "|found no listener:" coord event-name
