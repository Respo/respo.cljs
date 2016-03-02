
ns respo.renderer.render $ :require $ [] clojure.string :as string

def component-zero $ {} :tree nil :coord nil :events ({})
  , :children
  {}

def store $ atom $ []

def states $ atom $ {}

defn intent (action-name action-data)
  swap! store store

defn children-in-list? (children)
  if
    > (count children)
      , 0
    vector? $ first children
    , true

defn children-in-map? (children)
  if
    > (count children)
      , 0
    map? $ first children
    , false

defn children-list->map (children)
  into ({})
    map-indexed
      fn (index item)
        [] index item
      , children

defn render-component
  component props states coord
  let
    (state $ get states coord)
      initial-state $ :initial-state component
      render $ :render component
      instance $ render props state
    , assoc component-zero :tree instance :coord coord :events
    {}
    , :children
    {}

defn render-app (component)
  render-component component @store @states $ []
