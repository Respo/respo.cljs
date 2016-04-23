
ns respo.core
  :require-macros $ [] cljs.core.async.macros :refer ([] go)
  :require
    [] cljs.nodejs :as nodejs
    [] cljs.core.async :as a :refer $ [] >! <! chan
    [] respo.component.todolist :refer $ [] todolist-component
    [] respo.renderer.expander :refer $ [] render-app
    [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
    [] respo.update.core :refer $ [] update-transform
    [] respo.renderer.differ :refer $ [] find-element-diffs
    [] respo.util.time :refer $ [] io-get-time
    [] respo.controller.deliver :refer $ [] build-deliver-event do-states-gc
    [] respo.controller.resolver :refer $ [] get-markup-at
    [] respo.util.websocket :refer $ [] send-chan receive-chan
    [] respo.util.format :refer $ [] purify-element

defonce todolist-store $ atom
  []
    {} :text |101 :id 101
    {} :text |102 :id 102

defonce global-states $ atom ({})

defonce global-element $ atom nil

defonce clients-list $ atom ([])

defonce id-counter $ atom 10

defn dispatch (dispatch-type dispatch-data)
  println |dispatch: dispatch-type $ pr-str dispatch-data
  reset! id-counter $ inc @id-counter
  let
    (op-id @id-counter)
      new-store $ update-transform @todolist-store dispatch-type dispatch-data op-id
    println "|new store:" $ pr-str new-store
    reset! todolist-store new-store

defonce global-mutate-methods $ atom ({})

defn build-mutate (coord)
  if (contains? @global-mutate-methods coord)
    get @global-mutate-methods coord
    let
      (method $ fn (& state-args) (let ((component $ get-markup-at @global-element coord) (init-state $ :init-state component) (update-state $ :update-state component) (old-state $ if (contains? @global-states coord) (get @global-states coord) (apply init-state $ :args component)) (new-state $ apply update-state (cons old-state state-args))) (println "|compare states:" (pr-str @global-states) (pr-str old-state) (pr-str new-state)) (swap! global-states assoc coord new-state)))

      swap! global-mutate-methods assoc coord method
      , method

defn mount-demo ()
  let
    (todo-demo $ todolist-component ({} :tasks @todolist-store))
      element $ render-app todo-demo @global-states build-mutate

    -- println "|store to mount:" (pr-str @todolist-store)
      pr-str $ purify-element element
    reset! global-element element

defn rerender-demo ()
  let
    (todo-demo $ todolist-component ({} :tasks @todolist-store))
      element $ render-app todo-demo @global-states build-mutate
      changes $ find-element-diffs ([])
        []
        purify-element @global-element
        purify-element element

    reset! global-element element
    -- println |changes changes
    if
      not $ empty? changes
      do
        doall $ ->> @clients-list
          map $ fn (client-id)
            go $ >! send-chan
              [] client-id $ [] :patch changes

        do-states-gc global-states element

defn -main ()
  enable-console-print!
  println "|App is running..."
  mount-demo
  add-watch todolist-store :rerender rerender-demo
  add-watch global-states :rerender rerender-demo
  go $ loop
    [] acc $ {}
    let
      (msg-pack $ <! receive-chan)
        state-id $ :state-id (:meta msg-pack)
        msg-data $ :data msg-pack
        deliver-event $ build-deliver-event global-element dispatch

      -- println "|receiving message:" msg-pack
      case (:type msg-pack)
        :event $ do (apply deliver-event msg-data)
          recur acc
        :state/connect $ do
          swap! clients-list conj state-id
          >! send-chan $ [] state-id
            [] :sync $ purify-element @global-element
          recur acc

        :state/disconnect $ do
          reset! clients-list $ ->> @clients-list
            filter $ fn (client-id)
              not= client-id state-id

          recur acc

        println :else

set! *main-cli-fn* -main

defn on-jsload ()
  println |reload!
  rerender-demo
