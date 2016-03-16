
ns respo.controller.deliver $ :require $ respo.controller.resolver :refer $ [] find-event-target

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
    (all-coords $ distinct $ all-component-coords element)
      new-states $ purify-states ({})
        , @states-ref all-coords

    reset! states-ref new-states

defn build-set-state (states-ref coord)
  fn (state-updates)
    println "|update state:" (pr-str coord)
      pr-str state-updates
    swap! states-ref assoc coord state-updates

defn build-deliver-event
  element-ref intent states-ref rerender-handler
  fn (coord event-name simple-event)
    let
      (target-element $ find-event-target @element-ref coord event-name)
        target-listener $ get (:events target-element)
          , event-name

      if (some? target-listener)
        do
          println "|listener found:" coord event-name
          target-listener simple-event intent
            build-set-state states-ref $ :component-coord target-element
          rerender-handler

        println "|found no listener:" coord event-name
