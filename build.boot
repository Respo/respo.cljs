
(set-env!
  :asset-paths #{"assets/"}
  :resource-paths #{"polyfill" "src"}
  :dependencies '[])

(def +version+ "0.4.4")

(task-options!
  pom {:project     'respo/respo
       :version     +version+
       :description "A front-end MVC library"
       :url         "https://github.com/Respo/respo"
       :scm         {:url "https://github.com/Respo/respo"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

; some problems due to uglifying
(deftask build []
  (comp
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env! :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
