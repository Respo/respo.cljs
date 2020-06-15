
(defmacro defplugin [x params & body]
  (let [plugin-name (gensym "plugin-")]
    `(do
       (defn ~plugin-name [~@params] ~@body)
       (defn ~x [~@params] (respo.core/call-plugin-func ~plugin-name ~params)))))

(println (macroexpand-1 '(defplugin g [a b] (+ a b))))
