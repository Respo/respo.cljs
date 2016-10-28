
(set-env!
  :dependencies '[[org.clojure/clojure         "1.8.0"       :scope "provided"]
                  [org.clojure/clojurescript   "1.9.216"     :scope "provided"]
                  [adzerk/boot-cljs            "1.7.228-1"   :scope "test"]
                  [adzerk/boot-reload          "0.4.12"      :scope "test"]
                  [binaryage/devtools          "0.8.2"       :scope "test"]
                  [cirru/boot-stack-server     "0.1.19"      :scope "test"]
                  [adzerk/boot-test            "1.1.2"       :scope "test"]
                  [mvc-works/hsl               "0.1.2"       :scope "test"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[stack-server.core  :refer [start-stack-editor! transform-stack]]
         '[adzerk.boot-test   :refer :all])

(def +version+ "0.3.26")

(task-options!
  pom {:project     'respo/respo
       :version     +version+
       :description "A front-end MVC library"
       :url         "https://github.com/Respo/respo"
       :scm         {:url "https://github.com/Respo/respo"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask dev! []
  (set-env!
    :asset-paths #{"assets"}
    :resource-paths #{"polyfill/"})
  (comp
    (repl)
    (start-stack-editor! :extname ".cljc")
    (target :dir #{"src/"})
    (reload :on-jsload 'respo.main/on-jsload
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target)))

(deftask generate-code []
  (set-env!
    :resource-paths #{"polyfill/"})
  (comp
    (transform-stack :filename "stack-sepal.ir" :extname ".cljc")
    (target :dir #{"src/"})))

(deftask build-advanced []
  (set-env!
    :asset-paths #{"assets"}
    :source-paths #{"polyfill"})
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (target)))

(deftask rsync []
  (with-pre-wrap fileset
    (sh "rsync" "-r" "target/" "repo.respo.site:repo/Respo/respo" "--exclude" "main.out" "--delete")
    fileset))

; some problems due to uglifying
(deftask build []
  (set-env!
    :resource-paths #{"polyfill/"})
  (comp
    (transform-stack :filename "stack-sepal.ir" :extname ".cljc")
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env! :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))

(deftask watch-test []
  (set-env!
    :source-paths #{"src" "test"})
  (comp
    (watch)
    (test :namespaces '#{respo.html-test})))
