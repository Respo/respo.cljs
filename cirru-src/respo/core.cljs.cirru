
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.make-dom :refer $ [] make-element
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.expander :refer $ [] render-app
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.examples.dom-tree :refer $ [] diff-demos diff-props-demos
  [] respo.controller.manager :refer $ [] mount unmount
  [] respo.update.core :refer $ [] update-transform

defonce cached-tree $ atom nil

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defonce id-counter $ atom 10

declare mount-demo

defn intent (intent-name intent-data)
  .log js/console |intent: intent-name intent-data
  reset! id-counter $ inc @id-counter
  let
    (op-id @id-counter)
      new-store $ update-transform @todolist-store intent-name intent-data op-id
    .log js/console "|new store:" new-store
    reset! todolist-store new-store
    mount-demo

defn render-demo ()
  .clear js/console
  let
    (element-wrap $ render-app ([] todolist-component $ {} $ :tasks $ [] ({} :text |demo :id 1) ({} :text |demo2 :id 2)) ({}))
      tree $ :element element-wrap

    .log js/console tree
    let
      (html-in-string $ element->string tree)
      .log js/console html-in-string
      set!
        .-innerHTML $ .querySelector js/document |#app
        , html-in-string

    let
      (html-in-dom $ make-element tree)
        target $ .querySelector js/document |#app2
      .log js/console html-in-dom
      set! (.-innerHTML target)
        , |
      .appendChild target html-in-dom

    reset! cached-tree tree

defn mount-demo ()
  .log js/console "|store to mount:" @todolist-store
  let
    (todo-demo $ [] todolist-component $ {} :tasks @todolist-store)
      target $ .querySelector js/document |#app
    mount todo-demo target intent

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
