
ns respo.controller.manager $ :require
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.make-dom :refer $ [] make-element
  [] respo.renderer.patcher :refer $ [] apply-dom-changes
  [] respo.renderer.expander :refer $ [] render-app

defonce stateful-center $ atom $ {} :store ([])
  , :virtual-dom nil :is-mounted false :event-handlers
  {}

defn mount (markup mount-point)
  if (:is-mounted @stateful-center)
    let
      (new-tree $ make-element markup)
        changes $ find-element-diffs ([])
          []
          :virtual-dom @stateful-center
          , new-tree

      apply-dom-changes changes mount-point

    let
      (new-tree $ make-element markup)
        html-content $ element->string new-tree
        click-listener $ fn (event)
          .log js/console "|click happened:" event
        input-listener $ fn (event)
          .log js/console "|input happened:" event

      set! (.-innerHTML mount-point)
        , html-content
      .addEventListener mount-point |click click-listener
      .addEventListener mount-point |input input-listener
      swap! stateful-center assoc-in ([] :event-listener :click)
        , click-listener
      swap! stateful-center assoc-in ([] :event-listener :input)
        , input-listener
