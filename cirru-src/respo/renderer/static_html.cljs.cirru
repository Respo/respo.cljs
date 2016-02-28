
ns respo.renderer.static-html $ :require $ [] clojure.string :as string
defonce states $ atom $ {}
defonce memorization $ atom $ {}
declare markup->string
defn default-intent $ println "|Intent called..."
defn is-element
  markup
  and (vector? markup)
    keyword? $ first markup

defn is-component
  markup
  and (vector? markup)
    map? $ first markup

defn entry->string
  entry
  let
    (k $ first entry) (v $ last entry)
    str (name k)
      , |=
      pr-str v

defn props->string
  props
  ->> props
    filter $ fn
      entry
      let
        (k $ first entry)
        not $ re-matches
          re-pattern |^:on-.+
          str k

    map entry->string
    string/join "| "

defn element->string
  element
  let
    (tag $ name $ first element) (props $ props->string $ get element 1)
      children $ map markup->string $ drop 2 element
    str |< tag "| " props |>
      string/join | children
      , |</ tag |>

defn component->string
  component
  let
    (memory $ get @memorization component) (render-method $ :render $ first component)
      state $ :initial-state $ first component
      props $ last component
    if (and false $ some? memory)
      , memory
      let
        (factory $ render-method props state) (result $ factory default-intent)
        swap! memorization assoc component result
        element->string result

defn markup->string
  markup
  cond
    (string? markup) markup
      keyword? markup
      name markup
      is-element markup
      element->string markup
    (is-component markup) (component->string markup)
    (list? markup)
      ->> markup
        map markup->string
        string/join |

    :else "|Error: not recognized"
