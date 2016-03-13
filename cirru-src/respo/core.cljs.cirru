
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.expander :refer $ [] render-app
  [] respo.controller.resolver :refer $ [] find-event-target
  [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
  [] respo.update.core :refer $ [] update-transform
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.controller.client :refer $ [] initialize-instance activate-instance patch-instance release-instance
  [] respo.util.time :refer $ [] io-get-time
  [] respo.controller.deliver :refer $ [] build-deliver-event do-states-gc

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defonce global-states $ atom $ {}

defonce global-element $ atom nil

declare rerender-demo

defn mount-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      element-wrap $ render-app todo-demo @global-states
      app-root $ .querySelector js/document |#app
      deliver-event $ build-deliver-event global-element todolist-store global-states update-transform rerender-demo
    .log js/console "|store to mount:" @todolist-store
    initialize-instance app-root deliver-event
    activate-instance (:element element-wrap)
      , app-root deliver-event
    do-states-gc global-states $ :states element-wrap
    reset! global-element $ :element element-wrap

defn rerender-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      app-root $ .querySelector js/document |#app
      element-wrap $ render-app todo-demo @global-states
      changes $ find-element-diffs ([])
        []
        , @global-element
        :element element-wrap
      deliver-event $ build-deliver-event global-element todolist-store global-states update-transform rerender-demo

    patch-instance changes app-root deliver-event
    do-states-gc global-states $ :states element-wrap
    reset! global-element $ :element element-wrap

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
    reset! global-element nil
    mount-demo
