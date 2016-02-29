
ns respo.component.todolist $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl

def style-root $ {} (:color |black)
  :background-color $ hsl 120 20 93

def todolist $ {}
  :initial-state $ {} :draft |
  :render $ fn (props state)
    [] :div $ {} :style style-root
