(defproject mvc-works/respo "0.1.1"
  :description "Responsive DOM library"
  :url "https://github.com/mvc-works/respo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [mvc-works/hsl "0.1.2"]
                 [org.clojure/core.async "0.2.374"]]
  :plugins [[cirru/lein-sepal "0.0.17"]
            [lein-cljsbuild "1.1.2"]
            [lein-figwheel "0.5.0-6"]]
  :cirru-sepal {:paths ["cirru-src"]}
  :clean-targets ^{:protect false} [:target-path "target"]
  :target-path "target/%s"
  :cljsbuild {:builds {:server-dev {:source-paths ["src"]
                                    :figwheel {:on-jsload "respo.core/fig-reload"}
                                    :compiler {:main respo.core
                                               :output-to  "target/server_out/app.js"
                                               :output-dir "target/server_out/"
                                               :target :nodejs
                                               :optimizations :none
                                               :source-map true
                                               :verbose true}}
                       :server-prod {:source-paths ["src"]
                                     :compiler {:output-to "target/main.js"
                                                :target :nodejs
                                                :optimizations :advanced
                                                :pretty-print false}}}}
  :figwheel {:load-warninged-code false}
  :profiles {:uberjar {:aot :all}}
  :parallel-build true)
