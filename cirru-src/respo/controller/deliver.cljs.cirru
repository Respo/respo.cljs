
ns respo.controller.deliver $ :require $ respo.controller.resolver :refer $ [] find-event-target

defn do-states-gc (states-ref new-states)
  .info js/console "|states GC:" new-states
  reset! states-ref new-states

defn build-intent (store-ref updater)
  let
    (id-counter $ atom 10)
    fn (intent-name intent-data)
      .info js/console |intent: intent-name intent-data
      reset! id-counter $ inc @id-counter
      let
        (op-id @id-counter)
          new-store $ updater @store-ref intent-name intent-data op-id
        .log js/console "|new store:" new-store
        reset! store-ref new-store

defn build-set-state (states-ref coord)
  fn (state-updates)
    .info js/console "|update state:" coord state-updates
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
          target-listener simple-event (build-intent store-ref updater)
            build-set-state states-ref $ :component-coord target-element
          rerender-handler

        -- .info js/console "|found no listener:" coord event-name target-element
