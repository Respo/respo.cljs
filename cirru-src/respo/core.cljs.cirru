
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] element->string
  [] respo.renderer.virtual-dom :refer $ [] make-component
  [] respo.component.todolist :refer $ [] todolist-component
  [] respo.renderer.render :refer $ [] render-app

defn render-demo ()
  let
    (tree $ render-app $ [] todolist-component $ {} $ :tasks $ [] ({} :text |demo :id 1) ({} :text |demo2 :id 2))

    .clear js/console
    .log js/console tree
    .log js/console $ element->string tree

defn -main ()
  devtools/enable-feature! :sanity-hints :dirac
  devtools/install!
  enable-console-print!
  .log js/console "|App is running..."
  render-demo

set! js/window.onload -main

defn fig-reload (println |reload!)
  render-demo
