
ns respo.core $ :require ([] reagent.core :as r)
  [] devtools.core :as devtools

defn -main ()
  enable-console-print!
  devtools/set-pref! :install-sanity-hints true
  devtools/install!
  println "|App is running..."
set! js/window.onload -main
defn fig-reload () $ println |demo
