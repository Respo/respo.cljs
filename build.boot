
(set-env!
 :dependencies '[[org.clojure/clojure         "1.8.0"       :scope "provided"]
                 [org.clojure/clojurescript   "1.9.36"      :scope "provided"]
                 [adzerk/boot-cljs            "1.7.228-1"   :scope "test"]
                 [figwheel-sidecar            "0.5.2"       :scope "test"]
                 [com.cemerick/piggieback     "0.2.1"       :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.10"      :scope "test"]
                 [ajchemist/boot-figwheel     "0.5.2-2"     :scope "test"]
                 [cirru/boot-cirru-sepal      "0.1.5"       :scope "test"]
                 [org.clojure/core.async      "0.2.374"     :scope "test"]
                 [adzerk/boot-test            "1.1.1"       :scope "test"]
                 [mvc-works/hsl               "0.1.2"       :scope "test"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[cirru-sepal.core   :refer [transform-cirru]]
         '[adzerk.boot-test   :refer :all]
         '[boot-figwheel])

(def +version+ "0.1.22")

(task-options!
  pom {:project     'mvc-works/respo
       :version     +version+
       :description "Responsive DOM library"
       :url         "https://github.com/mvc-works/respo"
       :scm         {:url "https://github.com/mvc-works/respo"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask compile-cirru []
  (set-env!
    :source-paths #{"cirru/"})
  (comp
    (transform-cirru)
    (target :dir #{"compiled/"})))

(deftask watch-compile []
  (set-env!
    :source-paths #{"cirru/"})
  (comp
    (watch)
    (transform-cirru)
    (target :dir #{"compiled/"})))

(refer 'boot-figwheel :rename '{cljs-repl fw-cljs-repl}) ; avoid some symbols

(task-options!
  figwheel {:build-ids  ["dev"]
           :all-builds [{:id "dev"
                         :compiler {:main 'respo.core
                                    :target :nodejs
                                    :source-map true
                                    :optimizations :none
                                    :output-to "app.js"
                                    :output-dir "server_out/"
                                    :verbose false}
                         :figwheel {:build-id  "dev"
                                    :on-jsload 'respo.core/on-jsload
                                    :heads-up-display true
                                    :autoload true
                                    :target :nodejs
                                    :debug false}}]
           :figwheel-options {:repl true
                              :http-server-root "target"
                              :reload-clj-files false
                              :load-warninged-code false
                              :css-dirs ["target"]}})

(deftask dev []
  (set-env!
    :source-paths #{"compiled/src" "compiled/app"})
  (comp
    (repl)
    (figwheel)
    (target)))

(deftask build-simple []
  (set-env!
    :source-paths #{"cirru/src" "cirru/app"})
  (comp
    (transform-cirru)
    (cljs :compiler-options {:target :nodejs})
    (target)))

(deftask build-advanced []
  (set-env!
    :source-paths #{"cirru/src" "cirru/app"})
  (comp
    (transform-cirru)
    (cljs :optimizations :advanced :compiler-options {:target :nodejs})
    (target)))

(deftask rsync []
  (fn [next-task]
    (fn [fileset]
      (sh "rsync" "-r" "target/" "tiye:repo/mvc-works/respo" "--exclude" "main.out" "--delete")
      (next-task fileset))))

(deftask send-tiye []
  (comp
    (build-advanced)
    (rsync)))

; some problems due to uglifying
(deftask build []
  (set-env!
    :source-paths #{"cirru/src"})
  (comp
    (transform-cirru)
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
    :source-paths #{"cirru/src" "cirru/test"})
  (comp
    (watch)
    (transform-cirru)
    (test :namespaces '#{respo.html-test})))
