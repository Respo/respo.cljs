
ns respo.renderer.virtual-dom $ :require $ [] clojure.string :as string

defn is-element (markup)
  and (vector? markup)
    keyword? $ first markup

defn is-component (markup)
  and (vector? markup)
    map? $ first markup

defn make-element (markup coord)
  let
    (tag-name $ name $ first markup)
      props $ get markup 1
      children $ drop 2 markup
      element $ .createElement js/document tag-name
      child-elements $ map-indexed
        fn (index item)
          if (string? item)
            .createTextNode js/document item
            make-element item $ conj coord index

        , children

    doall $ map
      fn (entry)
        let
          (k $ name $ first entry)
            v $ last entry
          if
            some? $ re-find (re-pattern |^on-.+)
              , k
            aset element
              string/replace k |- |
              , v
            .setAttribute element k v

      , props

    doall $ map
      fn (child-element)
        .appendChild element child-element
      , child-elements

    , element

defn make-component (markup coord)
  let
    (component $ first markup)
      initial-state $ :initial-state component
      render-method $ :render component
      intent-method $ fn ()
        println "|intent called"
      factory $ render-method ({})
        , initial-state
      instance $ factory intent-method

    make-element instance coord
