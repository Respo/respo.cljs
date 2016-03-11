
ns respo.renderer.expander $ :require $ [] clojure.string :as string

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

defn io-get-time ()
  .valueOf $ js/Date.

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

defn render-markup
  markup old-states coord component-coord
  if (component? markup)
    render-component markup old-states coord
    render-element markup old-states coord component-coord

defn render-children
  acc children old-states coord component-coord
  -- .log js/console "|render children:" acc children old-states coord
  if
    = (count children)
      , 0
    , acc
    let
      (cursor $ first children)
        k $ key cursor
        v $ val cursor
      recur
        if (some? v)
          let
            (element-wrap $ render-markup v old-states (conj coord k) (, component-coord))

            {}
              :elements $ assoc (:elements acc)
                , k
                :element element-wrap
              :states $ merge (:states acc)
                :states :element-wrap

          , acc

        rest children
        , old-states coord component-coord

defn render-element
  markup old-states coord component-coord
  let
    (element-name $ first markup)
      props $ get markup 1
      raw-children $ subvec markup 2
      children $ if (children-in-map? raw-children)
        first raw-children
        children-list->map raw-children
      children-initial $ {} :states ({})
        , :elememts
        {}
      children-wrap $ render-children children-initial children old-states coord component-coord

    {}
      :states $ :states children-wrap
      :element $ {} (:name element-name)
        :props $ render-props props
        :events $ let
          (events $ render-events props)
          -- .log js/console |events: coord events props
          , events

        :coord coord
        :component-coord component-coord
        :children $ ->> (:elements children-wrap)
          sort-by first
          into $ sorted-map
        :duration nil

defn render-component (markup old-states coord)
  let
    (begin-time $ io-get-time)
      component $ first markup
      props $ get markup 1
      state $ if (contains? old-states coord)
        get old-states coord
        :initial-state component
      render $ :render component
      element $ render props state
      element-wrap $ render-element element old-states coord coord
      end-time $ io-get-time

    -- .log js/console "|component state:" coord state
    {}
      :states $ assoc (:states element-wrap)
        , coord state
      :element $ merge (:element element-wrap)
        {}
          :duration $ - end-time begin-time
          :component-name $ :name component

defn render-app (markup old-states)
  .info js/console "|render loop, old-states:" old-states
  render-component markup old-states $ []
