
ns respo.renderer.static-html $ :require
  [] clojure.string :as string
  [] respo.util.format :refer $ [] prop->attr

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
    str
      prop->attr $ name k
      , |=
      pr-str $ if (= k :style)
        style->string v
        , v

defn props->string (props)
  ->> props
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
      props $ :props element
      text-inside $ or (:innerHTML props)
        :inner-text props
      formatted-coord $ pr-str $ :coord element
      tailored-props $ -> (:props element)
        dissoc :innerHTML
        dissoc :inner-text
        merge $ {} :data-coord formatted-coord
      props-in-string $ props->string tailored-props
      children $ ->> (:children element)
        map $ fn (entry)
          element->string $ last entry

    str |< tag-name "| " props-in-string |>
      or text-inside $ string/join | children
      , |</ tag-name |>
