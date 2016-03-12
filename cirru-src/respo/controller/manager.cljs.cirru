
ns respo.controller.manager $ :require
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.renderer.patcher :refer $ [] apply-dom-changes
  [] respo.renderer.expander :refer $ [] render-app
  [] cljs.reader :refer $ [] read-string
  [] respo.controller.resolver :refer $ [] find-event-target
  [] respo.util.time :refer $ [] io-get-time
  [] respo.util.format :refer $ [] event->string
  [] respo.renderer.make-dom :refer $ [] make-element

defonce app-center $ atom $ {}

defn rerender-instance (element mount-point)
  let
    (instance $ get @app-center mount-point)
      changes $ find-element-diffs ([])
        []
        :element instance
        , element
      begin-time $ io-get-time

    .info js/console "|dom changes:" changes element
    -- breakpoint
    apply-dom-changes changes mount-point
    .info js/console "|time spent in patching:" $ - (io-get-time)
      , begin-time
    swap! app-center assoc mount-point $ assoc instance :element element
    .log js/console "|after rerender:" @app-center element

defn initialize-instance (element mount-point bubble-collection)
  let
    (dom-content $ make-element element)
    -- .log js/console "|DOM content:" dom-content
    set! (.-innerHTML mount-point)
      , |
    .appendChild mount-point dom-content
    doall $ ->> bubble-collection $ map $ fn (entry)
      let
        (event-string $ event->string $ name $ key entry)
          listener $ val entry
        .addEventListener mount-point event-string listener

    swap! app-center assoc mount-point $ merge $ {} (:listeners bubble-collection)
      :element element

defn build-set-state (coord update-state)
  fn (state-updates)
    update-state coord state-updates

defn read-coord (event)
  read-string $ ->> event (.-target)
    .-dataset
    .-coord

defn build-listener
  event-name mount-point intent update-state
  fn (event)
    let
      (coord $ read-coord event)
        target-element $ find-event-target
          :element $ get @app-center mount-point
          , coord event-name
        target-listener $ get (:events target-element)
          , event-name

      if (some? target-listener)
        target-listener event intent $ build-set-state (:component-coord target-element)
          , update-state
        .info js/console "|found no listener:" coord :on-click event target-element

defn mount
  markup mount-point intent global-states update-state do-states-gc
  let
    (no-bubble-events $ [] :on-scroll :on-focus :on-blur)
      bubble-events $ [] :on-click :on-input :on-wheel :on-keydown :on-dbclick :on-change
      no-bubble-collection $ ->> no-bubble-events
        map $ fn (event-name)
          [] event-name $ build-listener event-name mount-point intent update-state
        into $ {}

      bubble-collection $ ->> bubble-events
        map $ fn (event-name)
          [] event-name $ build-listener event-name mount-point intent update-state
        into $ {}

      element-wrap $ render-app markup global-states
      new-element $ :element element-wrap
      new-states $ :states element-wrap

    if (contains? @app-center mount-point)
      rerender-instance new-element mount-point
      initialize-instance new-element mount-point bubble-collection
    do-states-gc new-states

defn unmount (mount-point)
  if (contains? @app-center mount-point)
    do
      set! (.-innerHTML mount-point)
        , |
      doall $ ->>
        :events $ get @app-center mount-point
        map $ fn (entry)
          let
            (event-string $ event->string $ key entry)
              listener $ key entry
            .removeEventListener mount-point event-string listener

      swap! app-center dissoc mount-point
