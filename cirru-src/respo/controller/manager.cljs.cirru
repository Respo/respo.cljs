
ns respo.controller.manager $ :require
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.patcher :refer $ [] apply-dom-changes
  [] respo.renderer.expander :refer $ [] render-app
  [] cljs.reader :refer $ [] read-string
  [] respo.controller.resolver :refer $ [] find-event-target
  [] respo.util.time :refer $ [] io-get-time
  [] respo.util.format :refer $ [] event->string

defonce app-center $ atom $ {}

defn rerender-instance (markup mount-point)
  let
    (instance $ get @app-center mount-point)
      old-states $ :states instance
      element-wrap $ render-app markup old-states
      changes $ find-element-diffs ([])
        []
        :element instance
        :element element-wrap
      begin-time $ io-get-time

    .info js/console "|dom changes:" changes "|new states:" (:states element-wrap)
      , element-wrap
    -- breakpoint
    apply-dom-changes changes mount-point
    .info js/console "|time spent in patching:" $ - (io-get-time)
      , begin-time
    swap! app-center assoc mount-point $ merge instance element-wrap $ {} :markup markup
    .log js/console "|after rerender:" @app-center element-wrap

defn build-set-state (coord mount-point)
  fn (state-updates)
    -- .info js/console "|set state:" coord state-updates
    swap! app-center update-in
      [] mount-point :states coord
      fn (old-state)
        merge old-state state-updates

    rerender-instance
      :markup $ get @app-center mount-point
      , mount-point

defn read-coord (event)
  read-string $ ->> event (.-target)
    .-dataset
    .-coord

defn build-listener (event-name mount-point intent)
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
          , mount-point
        .info js/console "|found no listener:" coord :on-click event target-element

defn mount (markup mount-point intent)
  if (contains? @app-center mount-point)
    rerender-instance markup mount-point
    let
      (old-states $ {})
        no-bubble-events $ [] :on-scroll :on-focus :on-blur
        bubble-events $ [] :on-click :on-input :on-wheel :on-keydown :on-dbclick :on-change
        no-bubble-collection $ ->> bubble-events
          map $ fn (event-name)
            [] event-name $ build-listener event-name mount-point intent
          into $ {}

        bubble-collection $ ->> bubble-events
          map $ fn (event-name)
            [] event-name $ build-listener event-name mount-point intent
          into $ {}

        element-wrap $ render-app markup old-states
        html-content $ element->string $ :element element-wrap

      -- .log js/console "|HTML content:" html-content
      set! (.-innerHTML mount-point)
        , html-content
      doall $ ->> bubble-collection $ map $ fn (entry)
        let
          (k $ key entry)
            listener $ val entry
            event-string $ event->string k
          .addEventListener mount-point event-string listener

      swap! app-center assoc mount-point $ merge
        {} (:listeners bubble-collection)
          :markup markup
        , element-wrap

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
