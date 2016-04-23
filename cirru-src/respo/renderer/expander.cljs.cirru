
ns respo.renderer.expander $ :require
  [] clojure.string :as string
  [] respo.util.time :refer $ [] io-get-time
  [] respo.util.format :refer $ [] purify-element
  [] respo.alias :refer $ [] Component Element

defn keyword->string (x)
  subs (str x)
    , 1

defn component? (markup)
  = Component $ type markup

defn element? (markup)
  = Element $ type markup

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
  children states coord comp-coord
  -- .log js/console "|render children:" $ pr-str children
  ->> children
    map $ fn (child-entry)
      let
        (k $ key child-entry)
          child-element $ val child-entry
        [] k $ if (some? child-element)
          render-markup child-element states (conj coord k)
            , comp-coord
          , nil

    filter $ fn (entry)
      some? $ last entry
    into $ sorted-map

defn render-element
  markup states coord comp-coord
  let
    (children $ :children markup)
      child-elements $ render-children children states coord comp-coord
    -- .log js/console "|children should have order:" (pr-str children)
      pr-str child-elements
      pr-str markup
    assoc markup :coord coord :c-coord comp-coord :children child-elements

defn render-component (markup states coord)
  let
    (begin-time $ io-get-time)
      args $ :args markup
      component $ first markup
      init-state $ :init-state markup
      state $ if (contains? states coord)
        get states coord
        apply init-state args
      render $ :render markup
      half-render $ apply render args
      new-coord $ conj coord 0
      markup-tree $ half-render state
      tree $ render-element markup-tree states new-coord coord
      cost $ - (io-get-time)
        , begin-time

    -- .log js/console "|markup tree:" $ pr-str markup-tree
    assoc markup :coord coord :tree tree :cost cost

defn render-app (markup states)
  .info js/console "|render loop, states:" $ pr-str states
  render-markup markup states ([])
    []
