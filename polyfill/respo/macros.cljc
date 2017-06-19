
(ns respo.macros
  (:require [respo.alias :refer [create-comp create-element]]))

(defmacro defcomp [comp-name params & body]
  "
    (def comp-my
      (create-comp :comp-my
        (fn [x y]
          (fn [cursor] x))))

    becomes:

    (defcomp my-comp [x y] x)
  "
  `(def ~comp-name
    (create-comp ~(keyword comp-name)
      (~'fn [~@params]
        (~'fn [~'cursor] ~@body)))))

; (def support-elements '[a body br button canvas code div footer
;                         h1 h2 head header html hr img input li link
;                         option p pre script section select span style textarea title
;                         ul])

; (defn gen-dom-macro [el]
;   `(defn ~el [~'props & ~'children]
;     (create-element ~'props ~'children)))

; ; (defn gen-dom-macro [el]
; ;   `(defmacro ~el [~'props & ~'children] `(create-element (keyword ~el) ~'props (list ~@~'children))))

; (defmacro define-element-macro []
;   (println "called during compile time")
;   `(do ~@(clojure.core/map gen-dom-macro support-elements)))

; (define-element-macro)

(defmacro a         [props & children] `(create-element :a          ~props ~@children))
(defmacro body      [props & children] `(create-element :body       ~props ~@children))
(defmacro br        [props & children] `(create-element :br         ~props ~@children))
(defmacro button    [props & children] `(create-element :button     ~props ~@children))
(defmacro canvas    [props & children] `(create-element :canvas     ~props ~@children))
(defmacro code      [props & children] `(create-element :code       ~props ~@children))
(defmacro div       [props & children] `(create-element :div        ~props ~@children))
(defmacro footer    [props & children] `(create-element :footer     ~props ~@children))
(defmacro h1        [props & children] `(create-element :h1         ~props ~@children))
(defmacro h2        [props & children] `(create-element :h2         ~props ~@children))
(defmacro head      [props & children] `(create-element :head       ~props ~@children))
(defmacro header    [props & children] `(create-element :header     ~props ~@children))
(defmacro html      [props & children] `(create-element :html       ~props ~@children))
(defmacro hr        [props & children] `(create-element :hr         ~props ~@children))
(defmacro img       [props & children] `(create-element :img        ~props ~@children))
(defmacro input     [props & children] `(create-element :input      ~props ~@children))
(defmacro li        [props & children] `(create-element :li         ~props ~@children))
(defmacro link      [props & children] `(create-element :link       ~props ~@children))
(defmacro meta'     [props & children] `(create-element :meta       ~props ~@children))
(defmacro option    [props & children] `(create-element :option     ~props ~@children))
(defmacro p         [props & children] `(create-element :p          ~props ~@children))
(defmacro pre       [props & children] `(create-element :pre        ~props ~@children))
(defmacro script    [props & children] `(create-element :script     ~props ~@children))
(defmacro section   [props & children] `(create-element :section    ~props ~@children))
(defmacro select    [props & children] `(create-element :select     ~props ~@children))
(defmacro span      [props & children] `(create-element :span       ~props ~@children))
(defmacro style     [props & children] `(create-element :style      ~props ~@children))
(defmacro textarea  [props & children] `(create-element :textarea   ~props ~@children))
(defmacro title     [props & children] `(create-element :title      ~props ~@children))
(defmacro ul        [props & children] `(create-element :ul         ~props ~@children))
