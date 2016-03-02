
ns respo.component.todolist $ :require
  [] clojure.string :as string
  [] hsl.core :refer $ [] hsl
  [] respo.component.task :refer $ [] task-component

def style-root $ {} (:color |black)
  :background-color $ hsl 120 20 93

def style-list $ {} (:color |black)
  :background-color $ hsl 120 20 96

def style-toolbar $ {} (:display |flex)
  :flex-direction |row
  :justify-content |center

def style-button $ {}
  :background-color $ hsl 200 80 90
  :display |inline-block

defn clear-done (props state)
  fn (event intent set-state)
    intent-clear-done

defn on-text-change (props state)
  fn (event intent set-state)
    set-state $ {} :draft $ -> event (.-target)
      .-value

def todolist-component $ {}
  :initial-state $ {} :draft |
  :render $ fn (props state)
    let
      (tasks $ :tasks props)
      [] :div ({} :style style-root)
        [] :input $ {} :style style-input :value (:draft state)
          , :on-change
          on-text-change props state
          , :placeholder |Task
        [] :div ({} :style style-list)
          into ({})
            map
              fn (task)
                [] (:id task)
                  [] task-component $ {} :task task

              , tasks

        [] :div ({} :style style-toolbar)
          [] :div $ {} :style style-button :on-click $ clear-done props state
