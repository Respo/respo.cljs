
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.virtual-dom :refer $ [] make-element
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.render :refer $ [] render-app
  [] respo.renderer.differ :refer $ [] find-element-diffs

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

    .log js/console "|DOM diffs:" $ if (some? @cached-tree)
      find-element-diffs ([])
        , @cached-tree tree
      , "|not ready"

    reset! cached-tree tree

defn -main ()
  devtools/enable-feature! :sanity-hints :dirac
  devtools/install!
  enable-console-print!
  .log js/console "|App is running..."
  render-demo

set! js/window.onload -main

defn fig-reload (println |reload!)
  render-demo
