
ns respo.examples.dom-tree $ :require $ [] clojure.string :as string

def example-1 $ {} (:name :div)
  :attrs $ sorted-map
  :children $ sorted-map
  :coord $ []

def example-2 $ {} (:name :span)
  :attrs $ sorted-map
  :coord $ []
  :children $ sorted-map

def example-3 $ {} (:name :div)
  :attrs $ sorted-map :class |demo
  :coord $ []
  :children $ sorted-map

def example-4 $ {} (:name :div)
  :attrs $ sorted-map :class |another
  :coord $ []
  :children $ sorted-map

def example-5 $ {} (:name :div)
  :attrs $ sorted-map
  :coord $ []
  :children $ sorted-map 1 $ {} (:name :div)
    :attrs $ sorted-map
    :coord $ [] 1
    :children $ sorted-map
