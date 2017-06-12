
(ns respo.macros
  (:require [respo.alias :refer [create-comp]]))

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
