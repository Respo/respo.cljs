
ns respo.renderer.make-dom $ :require
  [] clojure.string :as string
  [] respo.renderer.static-html :refer $ [] style->string

defn make-element (virtual-element)
  let
    (tag-name $ name $ :name virtual-element)
      attrs $ :attrs virtual-element
      children $ :children virtual-element
      element $ .createElement js/document tag-name
      child-elements $ ->> children $ map $ fn (entry)
        let
          (item $ last entry)
          if (string? item)
            .createTextNode js/document item
            make-element item

    set!
      ->> element (.-dataset)
        .-coord
      pr-str $ :coord virtual-element

    doall $ ->> attrs $ map $ fn (entry)
      let
        (k $ name $ first entry)
          v $ last entry
        if
          some? $ re-find (re-pattern |^on-.+)
            , k
          aset element
            string/replace k |- |
            , v
          .setAttribute element k $ if (= k |style)
            style->string v
            , v

    doall $ ->> child-elements $ map $ fn (child-element)
      .appendChild element child-element
    , element
