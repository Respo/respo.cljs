
(defn read-password [guide]
  (String/valueOf (.readPassword (System/console) guide nil)))

(set-env!
  :resource-paths #{"polyfill" "src"}
  :dependencies '[[mvc-works/hsl        "0.1.2"   :scope "provided"]
                  [mvc-works/polyfill   "0.1.1"]]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                     :username "jiyinyiyong"
                                     :password (read-password "Clojars password: ")}]))

(def +version+ "0.7.2")

(deftask deploy []
  (comp
    (pom :project     'respo/respo
         :version     +version+
         :description "Respo: A virtual DOM library in ClojureScript"
         :url         "https://github.com/Respo/respo"
         :scm         {:url "https://github.com/Respo/respo"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (push :repo "clojars" :gpg-sign false)))
