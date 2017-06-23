
(ns respo.macros
  (:require [respo.core :refer [create-comp create-element]]))

(defmacro defcomp [comp-name params & body]
  "
    (def comp-my
      (create-comp :comp-my
        (fn [x y]
          (fn [cursor] x))))

    becomes:

    (defcomp my-comp [x y] x)
  "
  (assert (symbol? comp-name) "1st argument should be a symbol")
  (assert (coll? params) "2nd argument should be a collection")
  (assert (not (empty? body)) "should not return nil from component")
  `(def ~comp-name
    (create-comp ~(keyword comp-name)
      (~'fn [~@params]
        (~'fn [~'cursor] ~@body)))))

(def support-elements '[a body br button canvas code div footer
                        h1 h2 head header html hr img input li link
                        option p pre script section select span style textarea title
                        ul])

(defmacro meta' [props & children] `(create-element :meta ~props ~@children))

(defn gen-dom-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
     `(create-element ~(keyword '~el) ~~'props ~@~'children)))

(defmacro define-element-macro []
  `(do ~@(clojure.core/map gen-dom-macro support-elements)))

(define-element-macro)

(defmacro <> [el content style]
  `(~el {:inner-text ~content, :style ~style}))

(defmacro cursor-> [k component states & args]
  `(~'assoc (~component (~'get ~states ~k) ~@args) :cursor ~k))
