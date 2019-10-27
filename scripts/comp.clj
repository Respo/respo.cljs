(defn div [x])

(defn generate-component [comp-name params body]
  (println "inside" (pr-str comp-name) (pr-str params) (pr-str body))
  `(merge
    {:name ~(keyword comp-name),
     :render (fn [~@params]
               (fn [~'%cursor] ~@body))}))

(defmacro defcomp [comp-name params & body]
  (println "comp" (pr-str comp-name) (pr-str params) (pr-str body))
  (let [result (generate-component comp-name params body)]
   `(defmacro ~comp-name [~@params]
      (assoc ~result :args [~@params]))))

(println (macroexpand-1 (generate-component 'comp-a '[a] '(div {}))))

(println)

(println (macroexpand-1 '(defcomp comp-a [a] (div {}))))

(defcomp comp-a [a] (div {}))

(println)

(println (macroexpand-1 '(comp-a 2)))

(println)

(println (comp-a 2))
