
ns respo.renderer.alias

defrecord Element $ name coord c-coord attrs style event children

defrecord Component $ name coord args init-state update-state render tree cost

defn arrange-children (children)
  if
    = 1 $ count children
    let
      (cursor $ first children)
      if
        or
          = Element $ type cursor
          = Component $ type cursor
        map-indexed vector children
        , cursor

    ->> children (map-indexed vector)
      into $ sorted-map

defn create-element (tag-name props children)
  let
    (attrs $ if (contains? props :attrs) (into (sorted-map) (:attrs props)) (, {}))
      style $ if (contains? props :style)
        into (sorted-map)
          :style props
        , {}

      event $ if (contains? props :event)
        into (sorted-map)
          :event props
        , {}

      children-map $ arrange-children children

    ->Element tag-name nil nil attrs style event children-map

defn default-init (& args)
  , {}

def default-update merge

defn create-comp
  (comp-name render)
    create-comp comp-name default-init default-update render
  (comp-name init-state update-state render)
    fn (& args)
      ->Component comp-name nil args init-state update-state render nil nil

defn div (props & children)
  create-element :div props children

defn span (props & children)
  create-element :span props children

defn input (props & children)
  create-element :input props children

defn header (props & children)
  create-element :header props children

defn footer (props & children)
  create-element :footer props children
