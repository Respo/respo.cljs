
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.make-dom :refer $ [] make-element
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.expander :refer $ [] render-app
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.examples.dom-tree :refer $ [] diff-demos diff-attrs-demos
  [] respo.controller.manager :refer $ [] mount unmount

defonce cached-tree $ atom nil

defonce todolist-store $ atom $ []
  {} :text |demo1 :id 1
  {} :text |demo2 :id 2

defn intent (intent-name intent-data)
  .log js/console |intent: intent-name intent-data

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
  .clear js/console
  let
    (todo-demo $ [] todolist-component $ {} :tasks $ [] ({} :text |demo1 :id 1) ({} :text |demo2 :id 2))
      target $ .querySelector js/document |#app

    unmount target
    mount todo-demo target intent

defn -main ()
  devtools/enable-feature! :sanity-hints :dirac
  devtools/install!
  enable-console-print!
  .log js/console "|App is running..."
  mount-demo

set! js/window.onload -main

defn fig-reload ()
  .log js/console |reload!
  mount-demo
