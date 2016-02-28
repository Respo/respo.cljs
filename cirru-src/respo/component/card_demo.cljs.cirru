
ns respo.component.card-demo $ :require $ [] hsl.core :refer $ [] hsl
def card-demo $ {}
  :initial-state $ {}
    :is-open false
    :card-name :demo
  :render $ fn
    props state
    fn (intent)
      let
        (close-card $ fn (event) (, intent) ({} :is-open false))
          open-card $ fn
            event
            intent $ {} :is-open true
          connect $ fn
            key
            fn (event)
              intent $ {} :key $ -> event
                .-target
                .-value

        [] :div
          {}
          [] :div
            {}
            , |tag:
          if (:is-open state)
            [] :div
              {} :on-click close-card
              , "|it is open"
            [] :div
              {} :on-click open-card
              , "|it is closed"

          [] :input $ {} :value
            name $ :card-name state
            , :on-change
            connect :card-name
