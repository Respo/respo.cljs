
ns respo.component.task $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl
  [] respo.renderer.alias :refer $ [] div input span create-component

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
  fn (event dispatch mutate)
    .log js/console |clicked

defn handle-remove (props state)
  fn (event dispatch mutate)
    dispatch :remove $ :id (:task props)

defn on-text-change (props state)
  fn (event dispatch mutate)
    let
      (task-id $ :id (:task props))
        text $ :value event

      dispatch :update $ {} :id task-id :text text

def task-component $ create-component
  {} (:name :task)
    :render $ fn (props)
      fn (state)
        let
          (task $ :task props)
          div ({})
            input $ {} :value (:text task)
              , :on-input
              on-text-change props state
              , :style style-input
            span $ {} :style style-button :on-click (handle-remove props state)
              , :inner-text |Remove
