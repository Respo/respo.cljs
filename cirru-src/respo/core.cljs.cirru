
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.expander :refer $ [] render-app
  [] respo.controller.resolver :refer $ [] find-event-target
  [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
  [] respo.controller.manager :refer $ [] mount unmount
  [] respo.update.core :refer $ [] update-transform
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.controller.client :refer $ [] initialize-instance activate-instance patch-instance release-instance
  [] respo.util.time :refer $ [] io-get-time

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defonce global-states $ atom $ {}

defonce app-center $ atom $ {}

defonce id-counter $ atom 10

declare rerender-demo

defn intent (intent-name intent-data)
  .info js/console |intent: intent-name intent-data
  reset! id-counter $ inc @id-counter
  let
    (op-id @id-counter)
      new-store $ update-transform @todolist-store intent-name intent-data op-id
    .log js/console "|new store:" new-store
    reset! todolist-store new-store
    rerender-demo

defn do-states-gc (new-states)
  .info js/console "|states GC:" new-states
  reset! global-states new-states

defn build-set-state (coord update-state)
  fn (state-updates)
    .info js/console "|update state:" coord state-updates
    swap! global-states assoc coord state-updates
    rerender-demo

defn build-deliver-event (element)
  fn (coord event-name)
    let
      (target-element $ find-event-target element coord event-name)
        target-listener $ get (:events target-element)
          , event-name

      if (some? target-listener)
        target-listener event intent $ build-set-state (:component-coord target-element)
        . info js/console "|found no listener:" coord event-name target-element

defn mount-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      element-wrap $ render-app todo-demo @global-states
      app-root $ .querySelector js/document |#app
      deliver-event $ build-deliver-event (:element element-wrap)
    .log js/console "|store to mount:" @todolist-store
    initialize-instance app-root deliver-event
    activate-instance (:element element-wrap)
      , app-root deliver-event
    do-states-gc $ :states element-wrap

defn rerender-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      app-root $ .querySelector |#app
      old-element $ get-in @app-center $ [] mount-point :element
      element-wrap $ render-app todo-demo @global-states
      changes $ find-element-diffs ([])
        []
        , old-element
        :element element-wrap
      deliver-event $ build-deliver-event (:element element-wrap)

    patch-instance changes app-root deliver-event
    do-states-gc $ :states element-wrap

defn -main ()
  devtools/enable-feature! :sanity-hints :dirac
  devtools/install!
  enable-console-print!
  .log js/console "|App is running..."
  mount-demo

set! js/window.onload -main

defn fig-reload ()
  .clear js/console
  .log js/console |reload!
  let
    (app-root $ .querySelector js/document |#app)
    release-instance app-root

  mount-demo
