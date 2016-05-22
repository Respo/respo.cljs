
ns respo.html-test
  :require
    [] clojure.test :refer :all
    [] respo.alias :refer $ []
    [] respo.render.expander :refer $ [] render-app
    [] respo.component.todolist :refer $ [] todolist-component
    [] respo.controller.deliver :refer $ [] mutate-factory
    [] respo.render.static-html :refer $ [] element->string
    [] respo.util.format :refer $ [] purify-element

def todolist-store $ atom
  []
    {} :text |101 :id 101
    {} :text |102 :id 102

def global-states $ atom ({})

def global-element $ atom nil

def build-mutate $ mutate-factory global-element global-states

deftest html-test
  let
      todo-demo $ todolist-component ({} :tasks @todolist-store)
      element $ render-app todo-demo @global-states build-mutate
    testing "|test generated HTML"
      is $ = (slurp "|examples/demo.html") (element->string (purify-element element))
