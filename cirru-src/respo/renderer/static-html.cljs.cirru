
ns respo.renderer.static-html

defonce states $ atom $ {}
defonce memorization $ atom $ {}

defn is-element (segment)
  and
    vector? segment
    keyword? (get segment 0)

defn is-component (segment)
  and
    vector? segment
    map? (get segment 0)

defn entry->string (entry)
  let
      k $ first entry
      v $ last entry
    format "%s=%s" (pr-str k) (pr-str v)

defn props->string (props)
  ... props
    map entry->string
    join "| "

defn element->string (element)
  let
      tag $ first element
      props $ props->string (get element 2)
      children $ map markup->string (drop 2 element)
    format "|<%s %s>%s</%s>" tag props children tag

defn component->string (component)
  let
      memory $ get @memorization component
      render-method $ :render (first component)
      state $ :initial-state (first component)
      props $ last component
    if (some? memory) memory
      let
          result $ render-method props state
        swap! memorization component result
        , result

defn markup->string (markup)
  cond
    (is-element markup) (markup->string props)
    (is-component markup) (component->string props)
    (list? markup) $ ->> markup
      map recur
      join |
    :else "|Error: can not figure out"
