
ns respo.component.task $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl

defn on-text-change (props state)
  fn (event intent set-state)
    set-state $ {} :is-editing $ not $ :is-editing state

def task-component $ {}
  :initial-state $ {} :is-editing false
  :render $ fn (props state)
    let
      (task $ :task props)
      [] :div ({})
        [] :input $ {} :value (:text task)
          , :on-change
          on-text-change props state
