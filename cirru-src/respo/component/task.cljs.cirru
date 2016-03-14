
ns respo.component.task $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl

def style-input $ {} (:font-size |16px)
  :line-height |24px
  :padding "|0px 8px"
  :outline |none
  :min-width |300px

def style-button $ {} (:display |inline-block)
  :color $ hsl 40 80 100
  :background-color $ hsl 200 80 50
  :font-family |Verdana
  :padding "|0 6px"
  :cursor |pointer
  :border-radius |4px
  :margin-left |8px

defn on-click (props state)
  fn (event intent set-state)
    .log js/console |clicked

defn handle-remove (props state)
  fn (event intent set-state)
    intent :remove $ :id $ :task props

defn on-text-change (props state)
  fn (event intent set-state)
    let
      (task-id $ :id $ :task props)
        text $ :value event
      intent :update $ {} :id task-id :text text

def task-component $ {} (:name :task)
  :initial-state $ {}
  :render $ fn (props state)
    -- .log js/console "|task args" props state
    let
      (task $ :task props)
      [] :div ({})
        [] :input $ {} :value (:text task)
          , :on-input
          on-text-change props state
          , :style style-input
        [] :span $ {} :style style-button :on-click (handle-remove props state)
          , :inner-text |Remove
