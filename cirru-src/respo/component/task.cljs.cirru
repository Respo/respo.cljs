
ns respo.component.task $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl

def style-input $ {} (:font-size |16px)
  :line-height |24px
  :padding "|0px 8px"
  :outline |none

defn on-click (props state)
  fn (event intent set-state)
    .log js/console |clicked

defn on-text-change (props state)
  fn (event intent set-state)
    set-state $ {} :is-editing $ not $ :is-editing state

def task-component $ {}
  :initial-state $ {} :is-editing false
  :render $ fn (props state)
    -- .log js/console "|task args" props state
    let
      (task $ :task props)
      [] :div ({})
        [] :input $ {} :value (:text task)
          , :on-change
          on-text-change props state
          , :style style-input :on-click
          on-click props state
