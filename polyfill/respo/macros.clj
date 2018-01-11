
(ns respo.macros)

(defmacro defcomp [comp-name params & body]
  (assert (symbol? comp-name) "1st argument should be a symbol")
  (assert (coll? params) "2nd argument should be a collection")
  (assert (some? (last body)) "defcomp should return a component")
  `(defn ~comp-name [~@params]
    (merge respo.schema/component
      {:args (list ~@params) ,
       :name ~(keyword comp-name),
       :render (fn [~@params] (fn [~'%cursor] ~@body))})))

(def support-elements '[a body br button canvas code div footer
                        h1 h2 head header html hr img input li link
                        option p pre script section select span style textarea title
                        ul])

(defmacro meta' [props & children] `(respo.core/create-element :meta ~props ~@children))

(defn gen-dom-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
    `(respo.core/create-element ~(keyword '~el) ~~'props ~@~'children)))

(defmacro define-element-macro []
  `(do ~@(clojure.core/map gen-dom-macro support-elements)))

(define-element-macro)

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
