
(set-env!
 :source-paths #{"src"}
 :resource-paths #{"assets"}

 :dev-dependencies '[]
 :dependencies '[[org.clojure/clojure "1.8.0"           :scope "provided"]
                 [org.clojure/clojurescript "1.7.228"   :scope "provided"]
                 [adzerk/boot-cljs "1.7.170-3"      :scope "test"]
                 [figwheel-sidecar "0.5.2"          :scope "test"]
                 [com.cemerick/piggieback "0.2.1"   :scope "test"]
                 [org.clojure/tools.nrepl "0.2.10"  :scope "test"]
                 [ajchemist/boot-figwheel "0.5.2-0" :scope "test"]
                 [adzerk/boot-reload "0.4.6"        :scope "test"]
                 [cirru/boot-cirru-sepal "0.1.1"    :scope "test"]
                 [org.clojure/core.async "0.2.374"  :scope "test"]
                 [mvc-works/hsl "0.1.2"             :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[cirru-sepal.core :refer [cirru-sepal]]
         '[boot-figwheel])

(refer 'boot-figwheel :rename '{cljs-repl fw-cljs-repl}) ; avoid some symbols

(def +version+ "0.1.8")

(task-options!
  pom {:project     'mvc-works/respo
       :version     +version+
       :description "Responsive DOM library"
       :url         "https://github.com/mvc-works/respo"
       :scm         {:url "https://github.com/mvc-works/respo"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(set-env! :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))

(deftask gen-static []
  (comp
    (cirru-sepal :paths ["cirru-src"])
    (cljs :compiler-options {:target :nodejs})))

(task-options!
 figwheel {:build-ids  ["dev"]
           :all-builds [{:id "dev"
                         :compiler {:main 'respo.core
                                    :target :nodejs
                                    :source-map true
                                    :optimizations :none
                                    :output-to "app.js"
                                    :output-dir "server_out/"}
                         :figwheel {:build-id  "dev"
                                    :on-jsload 'respo.core/on-jsload
                                    :heads-up-display true
                                    :autoload true
                                    :target :nodejs
                                    :debug true}}]
           :figwheel-options {:repl true
                              :http-server-root "target"
                              :load-warninged-code false
                              :css-dirs ["target"]}})

(deftask dev []
  (comp
    (cirru-sepal :paths ["cirru-src"] :watch true)
    (repl)
    (figwheel)))

; bug: after optimization, method exported from npm package breaks
(deftask build-app []
  (comp
    (cirru-sepal :paths ["cirru-src"])
    (cljs :compiler-options {:target :nodejs} :optimizations :advanced)))

(deftask rsync []
  (fn [next-task]
    (fn [fileset]
        (sh "rsync" "-r" "target/" "tiye:repo/mvc-works/respo" "--exclude" "main.out" "--delete")
        (next-task fileset))))

(deftask send-tiye []
    (comp
        (build-app)
        (rsync)))

(deftask build []
  (comp
   (pom)
   (jar)
   (install)))

(deftask deploy []
  (comp
   (build)
   (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
