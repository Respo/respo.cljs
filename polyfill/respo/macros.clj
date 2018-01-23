
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

(def svg-elements '[svg animate circle defs ellipse font font font-face g
                    image line marker mask path pattern polygon polyline rect stop
                    text tspan view])

(defmacro meta' [props & children] `(respo.core/create-element :meta ~props ~@children))

(defmacro a' [props & children] `(respo.core/create-svg-element :a ~props ~@children))
(defmacro clip-path' [props & children] `(respo.core/create-svg-element :clipPath ~props ~@children))
(defmacro filter' [props & children] `(respo.core/create-svg-element :filter ~props ~@children))
(defmacro fe-blend [props & children] `(respo.core/create-svg-element :feBlend ~props ~@children))
(defmacro fe-offset' [props & children] `(respo.core/create-svg-element :feOffset ~props ~@children))
(defmacro style' [props & children] `(respo.core/create-svg-element :style ~props ~@children))

(defn gen-dom-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
    `(respo.core/create-element ~(keyword '~el) ~~'props ~@~'children)))
(defn gen-svg-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
    `(respo.core/create-svg-element ~(keyword '~el) ~~'props ~@~'children)))

(defmacro define-element-macro []
  `(do ~@(clojure.core/map gen-dom-macro support-elements)))
(defmacro define-svg-element-macro []
  `(do ~@(clojure.core/map gen-svg-macro svg-elements)))

(define-element-macro)
(define-svg-element-macro)

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

(defmacro svg-list->
  ([props children]
    `(respo.core/create-svg-list :g ~props ~children))
  ([tag props children]
    (assert (keyword? tag) "tag in list-> should be keyword")
    `(respo.core/create-svg-list ~tag ~props ~children)))

(defmacro action-> [op op-data]
  `(fn [~'%e d!# m!#]
    (d!# ~op ~op-data)))

(defmacro mutation-> [state]
  `(fn [~'%e d!# m!#]
    (m!# ~state)))
