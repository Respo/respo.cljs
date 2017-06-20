
(ns respo.macros
  (:require [respo.core :refer [create-comp create-element]]
            [respo.style :refer [style-value]]))

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

(def support-elements '[a body br button canvas code div footer
                        h1 h2 head header html hr img input li link
                        option p pre script section select span style textarea title
                        ul])

(defn gen-dom-macro [el]
  `(defmacro ~el [~'props ~'& ~'children]
     `(create-element ~(keyword '~el) ~~'props ~@~'children)))

(defmacro define-element-macro []
  `(do ~@(clojure.core/map gen-dom-macro support-elements)))

(define-element-macro)

(defmacro span-> [content style]
  `(span {:inner-text ~content, :style ~style}))

(defmacro code-> [content style]
  `(code {:inner-text ~content, :style ~style}))

(defmacro space-> [w h]
  (let [style (if (some? w)
                {:width w, :height "1px", :display :inline-block}
                {:width "1px", :height h, :display :inline-block})]
    `(div {:style ~style})))

(defmacro value-> [data more-style]
  `(div {:inner-text (pr-str ~data) :style (merge style-value ~more-style)}))
