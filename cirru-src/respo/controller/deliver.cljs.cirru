
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

defonce id-counter $ atom 10

defn build-intent (store-ref updater)
  fn (intent-name intent-data)
    println |intent: intent-name $ pr-str intent-data
    reset! id-counter $ inc @id-counter
    let
      (op-id @id-counter)
        new-store $ updater @store-ref intent-name intent-data op-id
      println "|new store:" $ pr-str new-store
      reset! store-ref new-store

defn build-set-state (states-ref coord)
  fn (state-updates)
    println "|update state:" (pr-str coord)
      pr-str state-updates
    swap! states-ref assoc coord state-updates

defn build-deliver-event
  element-ref store-ref states-ref updater rerender-handler
  fn (coord event-name simple-event)
    let
      (target-element $ find-event-target @element-ref coord event-name)
        target-listener $ get (:events target-element)
          , event-name

      if (some? target-listener)
        do
          println "|listener found:" coord event-name
          target-listener simple-event (build-intent store-ref updater)
            build-set-state states-ref $ :component-coord target-element
          rerender-handler

        println "|found no listener:" coord event-name
