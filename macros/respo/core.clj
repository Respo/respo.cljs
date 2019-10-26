
(ns respo.core)

(defmacro defcomp [comp-name params & body]
  (assert (symbol? comp-name) "1st argument should be a symbol")
  (assert (coll? params) "2nd argument should be a collection")
  (assert (some? (last body)) "defcomp should return a component")
  `(defn ~comp-name [~@params]
    (merge respo.schema/component
      {:args (list ~@params) ,
       :name ~(keyword comp-name),
       :render (fn [~@params]
                 (defn ~(symbol (str "call-" comp-name)) [~'%cursor] ~@body))})))

(def support-elements '[a body br button canvas code div footer
                        h1 h2 head header html hr i img input li link video audio
                        option p pre script section select span style textarea title
                        ul])

(defn confirm-item [x]
  `(respo.core/confirm-child ~x))

(defmacro meta' [props & children]
  `(respo.core/create-element :meta ~props ~@(map confirm-item children)))

(defn helper-create-el [el props children]
  `(respo.core/create-element ~(keyword el) ~props ~@(map confirm-item children)))

(defn gen-dom-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
    (helper-create-el '~el ~'props ~'children)))

(map gen-dom-macro support-elements)

(defmacro <>
  ([content] `(respo.core/create-element :span {:inner-text ~content}))
  ([content style] `(span {:inner-text ~content, :style ~style}))
  ([el content style] `(~el {:inner-text ~content, :style ~style})))

(defmacro cursor-> [k component states & args]
  `(assoc (~component (get ~states ~k) ~@args) :cursor (conj ~'%cursor ~k)))

(defmacro list->
  ([props children]
    `(respo.core/create-list-element :div ~props ~children))
  ([tag props children]
    (assert (keyword? tag) "tag in list-> should be keyword")
    `(respo.core/create-list-element ~tag ~props ~children)))

(defmacro action-> [op op-data]
  `(fn [~'%e d!# m!#]
    (d!# ~op ~op-data)))

(defmacro mutation-> [state]
  `(fn [~'%e d!# m!#]
    (m!# ~state)))

(defmacro defeffect [effect-name args params & body]
  (assert (and (sequential? args) (every? symbol? args)) "args should be simple sequence")
  (assert (and (sequential? params) (every? symbol? params)) "params supported to be [action el *local]")
  `(defn ~effect-name [~@args]
    (merge respo.schema/effect
     {:name ~(keyword effect-name)
      :args [~@args]
      :coord []
      :method (fn [[~@args] [~@params]]
                ~@(if (empty? body)
                  `((js/console.warn (str "WARNING: " '~effect-name " has no code for handling effects!")))
                  body))})))
