
ns respo.core $ :require ([] reagent.core :as r)
  [] devtools.core :as devtools
  [] respo.renderer.static-html :refer $ [] markup->string
  [] respo.component.card-demo :refer $ [] card-demo

defn render-demo () $ let
  (demo-in-html (markup->string $ [] card-demo ({})))

  println demo-in-html

defn -main ()
  enable-console-print!
  devtools/set-pref! :install-sanity-hints true
  devtools/install!
  println "|App is running..."
  render-demo
set! js/window.onload -main
defn fig-reload ()
  println |reload!
  render-demo
