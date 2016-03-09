
ns respo.renderer.make-dom $ :require
  [] clojure.string :as string
  [] respo.renderer.static-html :refer $ [] style->string
  [] respo.util.format :refer $ [] dashed->camel

defn make-element (virtual-element)
  let
    (tag-name $ name $ :name virtual-element)
      props $ :props virtual-element
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

    doall $ ->> props
      filter $ fn (entry)
        let
          (k $ name $ key entry)
          =
            re-find (re-pattern |^on-.+)
              , k
            , nil

      map $ fn (entry)
        let
          (k $ dashed->camel $ name $ first entry)
            v $ last entry
          .setAttribute element k $ if (= k |style)
            style->string v
            , v
          aset element k $ if (= k |style)
            style->string v
            , v

    doall $ ->> child-elements $ map $ fn (child-element)
      .appendChild element child-element
    , element
