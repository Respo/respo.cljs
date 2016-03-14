
ns respo.core
  :require-macros
    [] cljs.core.async.macros :refer
      [] go
  :require
    [] cljs.nodejs :as nodejs
    [] cljs.core.async :as a :refer $ [] >! <! chan
    [] respo.component.todolist :refer $ [] todolist-component
    [] respo.renderer.expander :refer $ [] render-app
    [] respo.controller.resolver :refer $ [] find-event-target
    [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
    [] respo.update.core :refer $ [] update-transform
    [] respo.renderer.differ :refer $ [] find-element-diffs
    [] respo.util.time :refer $ [] io-get-time
    [] respo.controller.deliver :refer $ [] build-deliver-event do-states-gc
    [] respo.util.websocket :refer $ [] send-chan receive-chan

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defonce global-states $ atom $ {}

defonce global-element $ atom nil
defonce clients-list $ atom $ []

declare rerender-demo

defn mount-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      element-wrap $ render-app todo-demo @global-states
    println "|store to mount:" (pr-str @todolist-store)
    do-states-gc global-states $ :states element-wrap
    reset! global-element $ :element element-wrap

defn rerender-demo ()
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      element-wrap $ render-app todo-demo @global-states
      changes $ find-element-diffs ([])
        []
        , @global-element
        :element element-wrap
    do-states-gc global-states $ :states element-wrap
    reset! global-element $ :element element-wrap
    go
      doall $ ->> clients-list $ map $ fn (client-id)
        >! send-chan $ [] client-id $ [] :patch changes

defn -main ()
  enable-console-print!
  println "|App is running..."
  mount-demo
  go $ loop
    [] acc ({})
    let
        msg-pack $ <! receive-chan
        state-id $ :state-id $ :meta msg-pack
        msg-data $ :data msg-pack
        deliver-event $ build-deliver-event global-element todolist-store global-states update-transform rerender-demo
      println "|receiving message:" msg-pack
      case (:type msg-pack)
        :event $ do
          apply deliver-event msg-data
          recur acc
        :state/connect $ do
          swap! clients-list conj state-id
          >! send-chan $ [] state-id $ [] :sync @global-element
          recur acc
        :state/disconnect $ do

          reset! clients-list $ ->> @clients-list $ filter $ fn (client-id)
            not= client-id state-id
          recur acc
        println :else

set! *main-cli-fn* -main

defn fig-reload ()
  println |reload!
