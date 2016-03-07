
ns respo.examples.dom-tree $ :require
  [] clojure.string :as string
  [] respo.renderer.differ :refer $ [] find-element-diffs

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

def example-6 $ {} (:name :div)
  :attrs $ sorted-map :class |example-6
  :coord $ []
  :children $ sorted-map 1 $ {} (:name :div)
    :attrs $ sorted-map :style $ sorted-map :color |red
    :coord $ [] 1
    :children $ sorted-map

def example-7 $ {} (:name :div)
  :attrs $ sorted-map :class |example-7 :spell-check false
  :coord $ []
  :children $ sorted-map 1 $ {} (:name :div)
    :attrs $ sorted-map :style $ sorted-map :color |yellow :display |inline-block
    :coord $ [] 1
    :children $ sorted-map 0 $ {} (:name :span)
      :attrs $ sorted-map
      :coord $ [] 1 0
      :children $ sorted-map

defn diff-demos ()
  .clear js/console
  .log js/console "|DOM diff 1->2:" $ find-element-diffs ([])
    []
    , example-1 example-2
  newline
  .log js/console "|DOM diff 1->3:" $ find-element-diffs ([])
    []
    , example-1 example-3
  newline
  .log js/console "|DOM diff 1->4:" $ find-element-diffs ([])
    []
    , example-1 example-4
  newline
  .log js/console "|DOM diff 1->5:" $ find-element-diffs ([])
    []
    , example-1 example-5
  newline
  .log js/console "|DOM diff 3->4:" $ find-element-diffs ([])
    []
    , example-3 example-4
  newline
  .log js/console "|DOM diff 3->5:" $ find-element-diffs ([])
    []
    , example-3 example-5
  newline
  .log js/console "|DOM diff 6->7:" $ find-element-diffs ([])
    []
    , example-6 example-7
