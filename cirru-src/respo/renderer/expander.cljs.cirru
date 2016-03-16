
ns respo.renderer.expander $ :require
  [] clojure.string :as string
  [] respo.util.time :refer $ io-get-time

def states $ atom $ {}

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

defn sort-style (styles)
  ->> styles (sort-by key)
    into $ sorted-map

defn render-props (props)
  ->> props
    filter $ fn (entry)
      let
        (prop-name $ keyword->string $ first entry)
        not $ re-find (re-pattern |^on-.+)
          , prop-name

    map $ fn (entry)
      let
        (k $ first entry)
          v $ last entry
          prop-name $ keyword->string k
        if (= prop-name |style)
          [] k $ sort-style v
          , entry

    sort-by key
    into $ sorted-map

defn render-events (props)
  ->> props
    filter $ fn (entry)
      let
        (prop-name $ name $ key entry)
        re-find (re-pattern |^on-.+)
          , prop-name

    sort-by key
    into $ sorted-map

declare render-component

declare render-element

defn vector-contains? (outer-vec inner-vec)
  cond
    (and (>= (count outer-vec) (, 0)) (= (count inner-vec) (, 0))) true

    (and (= (count outer-vec) (, 0)) (> (count inner-vec) (, 0))) false

    :else $ if
      = (first outer-vec)
        first inner-vec
      recur (subvec outer-vec 1)
        subvec inner-vec 1
      , false

defn filter-states (partial-states coord)
  ->> partial-states
    filter $ fn (entry)
      vector-contains? (key entry)
        , coord

    into $ {}

defn render-markup
  markup partial-states coord component-coord
  if (component? markup)
    render-component markup (filter-states partial-states coord)
      , coord
    render-element markup partial-states coord component-coord

defn render-children
  children global-states coord component-coord
  -- .log js/console "|render children:" children global-states coord
  ->> children
    map $ fn (child-entry)
      let
        (k $ key child-entry)
          child-element $ val child-entry
        [] k $ if (some? child-element)
          render-markup child-element global-states (conj coord k)
            , component-coord
          , nil

    sort-by first
    into $ sorted-map

defn render-element
  markup old-states coord component-coord
  let
    (element-name $ first markup)
      props $ get markup 1
      raw-children $ subvec markup 2
      children $ if (children-in-map? raw-children)
        first raw-children
        children-list->map raw-children
      child-elements $ render-children children old-states coord component-coord

    {} (:name element-name)
      :props $ render-props props
      :events $ let
        (events $ render-events props)
        -- .log js/console |events: coord events props
        , events

      :coord coord
      :component-coord component-coord
      :children child-elements
      :duration nil

defn render-component (markup old-states coord)
  let
    (begin-time $ io-get-time)
      component $ first markup
      props $ get markup 1
      state $ merge (:initial-state component)
        if (contains? old-states coord)
          get old-states coord
          , nil

      render $ :render component
      element $ render props state
      element $ render-element element old-states coord coord
      end-time $ io-get-time

    -- .log js/console "|component state:" coord state
    assoc element :duration (- end-time begin-time)
      , :component-name
      :name component

defn render-app (markup old-states)
  .info js/console "|render loop, old-states:" $ pr-str old-states
  render-component markup old-states $ []