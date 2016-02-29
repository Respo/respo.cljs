
ns respo.core $ :require
  [] reagent.core :as r
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] markup->string
  [] respo.renderer.virtual-dom :refer $ [] make-component
  [] respo.component.card-demo :refer $ [] card-demo

defn render-demo ()
  let
    (demo-in-html $ markup->string $ [] card-demo $ {})
      demo-in-dom $ make-component
        [] card-demo $ {}
        []
    
    -- println demo-in-html
    .log js/console demo-in-dom

defn -main ()
  enable-console-print!
  devtools/set-pref! :install-sanity-hints true
  devtools/install!
  println "|App is running..."
  render-demo

set! js/window.onload -main

defn fig-reload (println |reload!)
  render-demo
