
ns respo.util.format $ :require $ [] clojure.string :as string

defn dashed->camel
  (x)
    dashed->camel | x false
  (acc piece promoted?)
    if (= piece |)
      , acc
      let
        (cursor $ get piece 0)
          piece-followed $ subs piece 1
        if (= cursor |-)
          recur acc piece-followed true
          recur
            str acc $ if promoted? (string/upper-case cursor)
              , cursor
            , piece-followed false

defn prop->attr (x)
  case x (|class-name |class)
    , x

defn event->string (x)
  subs (name x)
    , 3
