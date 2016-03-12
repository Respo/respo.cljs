
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
  [] respo.controller.manager :refer $ [] mount unmount
  [] respo.update.core :refer $ [] update-transform

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defonce global-states $ atom $ {}

defonce id-counter $ atom 10

declare mount-demo

defn intent (intent-name intent-data)
  .info js/console |intent: intent-name intent-data
  reset! id-counter $ inc @id-counter
  let
    (op-id @id-counter)
      new-store $ update-transform @todolist-store intent-name intent-data op-id
    .log js/console "|new store:" new-store
    reset! todolist-store new-store
    mount-demo

defn update-state (coord state-updates)
  .info js/console "|update state:" coord state-updates
  swap! global-states assoc coord state-updates
  mount-demo

defn do-states-gc (new-states)
  .info js/console "|states GC:" new-states
  reset! global-states new-states

defn mount-demo ()
  .log js/console "|store to mount:" @todolist-store
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      target $ .querySelector js/document |#app
    mount todo-demo target intent @global-states update-state do-states-gc

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
    (target $ .querySelector js/document |#app)
    unmount target

  mount-demo
