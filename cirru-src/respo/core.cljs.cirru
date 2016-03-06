
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.virtual-dom :refer $ [] make-element
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.render :refer $ [] render-app
  [] respo.renderer.differ :refer $ [] find-element-diffs
  [] respo.examples.dom-tree :as examples

defonce cached-tree $ atom nil

defn render-demo ()
  .clear js/console
  let
    (tree $ render-app $ [] todolist-component $ {} $ :tasks $ [] ({} :text |demo :id 1) ({} :text |demo2 :id 2))

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

defn diff-demos ()
  .clear js/console
  .log js/console "|DOM diff 2:" $ find-element-diffs ([])
    , examples/example-1 examples/example-2
  .log js/console "|DOM diff 3:" $ find-element-diffs ([])
    , examples/example-1 examples/example-3
  .log js/console "|DOM diff 4:" $ find-element-diffs ([])
    , examples/example-1 examples/example-4

defn -main ()
  devtools/enable-feature! :sanity-hints :dirac
  devtools/install!
  enable-console-print!
  .log js/console "|App is running..."
  render-demo
  diff-demos

set! js/window.onload -main

defn fig-reload (println |reload!)
  diff-demos
