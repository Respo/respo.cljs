
(defmacro defeffect [effect-name args old-args params & body]
  ; (assert (and (sequential? args) (every? symbol? args)) "args should be simple sequence")
  ; (assert (and (sequential? old-args) (every? symbol? old-args)) "old args should be sequence")
  ; (assert (and (sequential? params) (every? symbol? params) (= 2 (count params))) "params 2 args")
  `(defn ~effect-name [~@args]
    (merge respo.schema/effect
     {:name ~(keyword effect-name)
      :args ~args
      :coord []
      :old-args []
      :method (fn [[~@args] [~@old-args] [~'action ~'parent]] ~@body)})))

(println
  (macroexpand-1
    '(defeffect effect-a [a b] [a' b'] [action parent] (println a b action))))
