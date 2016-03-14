
ns respo.component.todolist $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl
  [] respo.component.task :refer $ [] task-component

def style-root $ {} (:color |black)
  :background-color $ hsl 120 20 93
  :line-height |24px
  :font-size |16px
  :padding |10px

def style-list $ {} (:color |black)
  :background-color $ hsl 120 20 96

def style-input $ {} (:font-size |16px)
  :line-height |24px
  :padding "|0px 8px"
  :outline |none
  :min-width |300px

def style-toolbar $ {} (:display |flex)
  :flex-direction |row
  :justify-content |center
  :width |300px
  :padding "|4px 0"

def style-button $ {}
  :background-color $ hsl 200 80 90
  :display |inline-block
  :padding "|0 6px 0 6px"
  :font-family |Verdana
  :cursor |pointer
  :border-radius |4px
  :margin-left |8px

def style-panel $ {} $ :display |flex

defn clear-done (props state)
  fn (event intent set-state)
    .log js/console "|intent clear-done"
    intent :clear nil

defn on-focus (props state)
  fn (event intent set-state)
    .log js/console "|Just focused~"

defn on-text-change (props state)
  fn (simple-event intent set-state)
    set-state $ {} :draft $ :value simple-event

defn handle-add (props state)
  .log js/console "|state built inside:" (pr-str props)
    pr-str state
  fn (event intent set-state)
    .log js/console "|click add!" props state
    intent :add $ :draft state
    set-state $ {} :draft |

def todolist-component $ {} (:name :todolist)
  :initial-state $ {} :draft |
  :render $ fn (props state)
    let
      (tasks $ :tasks props)
      .log js/console |tasks: $ pr-str tasks
      [] :div ({} :style style-root)
        [] :div ({} :style style-panel)
          [] :input $ {} :style style-input :value (:draft state)
            , :on-input
            on-text-change props state
            , :on-focus
            on-focus props state
            , :placeholder |Task
          [] :div ({} :style style-button)
            [] :span $ {} :inner-text |Add :on-click $ handle-add props state
          [] :div $ {} :style style-button :on-click (clear-done props state)
            , :inner-text |Clear

        [] :div
          {} :class-name |task-list :style style-list
          into ({})
            ->> tasks $ map $ fn (task)
              [] (:id task)
                [] task-component $ {} :task task

        if
          > (count tasks)
            , 0
          [] :div ({} :style style-toolbar)
            [] :div $ {} :style style-button :on-click (clear-done props state)
              , :inner-text |Clear
