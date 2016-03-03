
ns respo.renderer.render $ :require $ [] clojure.string :as string

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
    ->> children $ map-indexed $ fn (index item)
      [] index item

defn keyword->string (x)
  subs (str x)
    , 1

defn component? (markup)
  and (vector? markup)
    map? $ first markup

defn element? (markup)
  and (vector? markup)
    keyword? $ first markup

declare render-component

declare render-element

defn render-markup (markup states coord)
  if (component? markup)
    render-component markup states coord
    render-element markup states coord

defn style->string (styles)
  , styles

defn render-element (markup states coord)
  let
    (element-name $ first markup)
      props $ get markup 1
      raw-children $ subvec markup 2
      children $ if (children-in-map? raw-children)
        first raw-children
        children-list->map raw-children

    {} (:name element-name)
      :attrs $ into ({})
        ->> props
          filter $ fn (entry)
            let
              (attr-name $ keyword->string $ first entry)
              not $ re-find (re-pattern |^on-.+)
                , attr-name

          map $ fn (entry)
            let
              (k $ first entry)
                v $ last entry
                attr-name $ keyword->string k
              if (= attr-name |style)
                [] k $ style->string v
                , entry

      :events $ into ({})
        ->> props $ filter $ fn (entry)
          let
            (attr-name $ keyword->string $ first entry)
            and false $ re-find (re-pattern |^on-.+)
              , attr-name

      :coord coord
      :children $ into ({})
        ->> children
          map $ fn (entry)
            let
              (k $ first entry)
                v $ last entry
              [] k $ if (some? v)
                render-markup v states $ conj coord k
                , nil

          sort-by first

defn render-component (markup states coord)
  let
    (component $ first markup)
      props $ last markup
      state $ get states coord
      initial-state $ :initial-state component
      render $ :render component
      element $ render props state
    render-element element states coord

defn render-app (component)
  render-component component @states $ []
