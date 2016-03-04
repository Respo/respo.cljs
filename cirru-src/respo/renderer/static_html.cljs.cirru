
ns respo.renderer.static-html $ :require $ [] clojure.string :as string

defn style->string (styles)
  string/join | $ ->> styles $ map $ fn (entry)
    let
      (k $ first entry)
        v $ last entry
      str (name k)
        , |: v |;

defn entry->string (entry)
  let
    (k $ first entry)
      v $ last entry
    str (name k)
      , |=
      pr-str $ if (= k :style)
        style->string v
        , v

defn attrs->string (attrs)
  ->> attrs
    filter $ fn (entry)
      let
        (k $ first entry)
        not $ re-matches (re-pattern |^:on-.+)
          str k

    map entry->string
    string/join "| "

defn element->string (element)
  let
    (tag-name $ name $ :name element)
      attrs $ attrs->string $ merge (:attrs element)
        {} :data-coord $ pr-str $ :coord element
      children $ ->> (:children element)
        map $ fn (entry)
          element->string $ last entry

    str |< tag-name "| " attrs |> (string/join | children)
      , |</ tag-name |>
