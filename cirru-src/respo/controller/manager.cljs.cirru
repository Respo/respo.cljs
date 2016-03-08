
ns respo.controller.manager $ :require
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.patcher :refer $ [] apply-dom-changes
  [] respo.renderer.expander :refer $ [] render-app
  [] cljs.reader :refer $ [] read-string
  [] respo.controller.resolver :refer $ [] find-event-target

defonce app-center $ atom $ {}

defn rerender-instance (mount-point)
  let
    (instance $ get @app-center mount-point)
      old-states $ :states instance
      markup $ :markup instance
      element-wrap $ render-app markup old-states
      changes $ find-element-diffs ([])
        []
        :virtual-dom instance
        :element element-wrap

    .log js/console "|dom changes:" changes
    apply-dom-changes changes mount-point
    swap! app-center assoc mount-point $ merge instance element-wrap

defn build-set-state (coord mount-point)
  fn (state-updates)
    swap! app-center update-in
      [] mount-point :states coord
      fn (old-state)
        merge old-state state-updates

    rerender-instance mount-point

defn read-coord (event)
  read-string $ ->> event (.-target)
    .-dataset
    .-coord

defn mount (markup mount-point intent)
  if (contains? @app-center mount-point)
    rerender-instance mount-point
    let
      (old-states $ {})
        element-wrap $ render-app markup old-states
        html-content $ element->string $ :element element-wrap
        click-listener $ fn (event)
          let
            (coord $ read-coord event)
              target-element $ find-event-target (:element element-wrap)
                , coord :on-click
              target-listener $ :on-click $ :events target-element

            if (some? target-listener)
              target-listener event intent $ build-set-state coord mount-point
              .log js/console "|found no listener:" coord event

        input-listener $ fn (event)
          let
            (coord $ read-coord event)
              target-element $ find-event-target (:element element-wrap)
                , coord :on-input
              target-listener $ :on-input $ :event target-element

            if (some? target-listener)
              target-listener $ :on-input $ build-set-state coord mount-point
              .log js/console "|found no listener:" coord event

      set! (.-innerHTML mount-point)
        , html-content
      .addEventListener mount-point |click click-listener
      .addEventListener mount-point |input input-listener
      swap! app-center assoc mount-point $ merge
        {}
          :listeners $ {} (:on-click click-listener)
            :on-input input-listener
          :markup markup

        , element-wrap

defn unmount (mount-point)
  if (contains? @app-center mount-point)
    do
      set! (.-innerHTML mount-point)
        , |
      .removeEventListener mount-point |click $ get-in @app-center $ [] mount-point :listeners :on-click
      .removeEventListener mount-point |input $ get-in @app-center $ [] mount-point :listeners :on-input
      swap! app-center dissoc mount-point
