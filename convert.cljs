
(ns convert-stack.core
  (:require [planck.shell :refer [sh]]
            [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [planck.core :refer [*command-line-args* spit slurp]]))

(def class-dir "cirru/src")

(defn filter-ir [paths]
  (->> paths
    (filter (fn [path]
      (string/ends-with? path "ir")))))

(def all-files
  (-> (sh "find" class-dir)
    (:out)
    (string/trim)
    (string/split "\n")
    (filter-ir)))

(def def-names #{"defn" "def" "defonce" "defrecord"})

(defn load-file-data [file-name]
  (let
    [data (read-string (slurp file-name))]
    (println (pr-str file-name))
    {:ns (-> data (get 0) (get 1))
     :namespaces (first data)
     :definitions
      (->> (rest data)
        (filter (fn [line]
          (contains? def-names (first line))))
        (into []))
     :procedures
      (->> (rest data)
        (filter (fn [line]
          (not (contains? def-names (first line)))))
        (into []))}))

(def initial-sepal
  {:namespaces {}
   :definitions {}
   :procedures {}})

(def sepal-data
  (reduce
    (fn [acc file-data]
      (-> acc
        (assoc-in [:namespaces (:ns file-data)] (:namespaces file-data))
        (assoc-in [:procedures (:ns file-data)] (:procedures file-data))
        (update :definitions
          (fn [definitions]
            (let [pairs
                  (->> (:definitions file-data)
                    (map (fn [def-line]
                                        [(str (:ns file-data) "/" (get def-line 1)) def-line])))]
              (merge definitions (into {} pairs)))))))
    initial-sepal
    (map load-file-data all-files)))

(spit "stack-sepal.ir" (pr-str sepal-data))