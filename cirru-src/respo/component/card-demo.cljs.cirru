
ns respo.component.card-demo

def card-demo $ {}

  :initial-state $ {}
    :is-open false
    :card-name :demo

  :render $ fn (props state)
    fn (intent)
      let
          close-card $ fn (event)
            intent $ {} :is-open false
          open-card $ fn (event)
            intent $ {} :is-open true
          connect $ fn (key)
            fn (event)
              intent $ {} :key (-> event (.-target) (.-value))

        [] :div ({})
          cond (:is-open state)
            [] :div ({} :on-click close-card) "|state is open"
            [] :div ({} :on-click open-card) "|state is closed"
          [] :input ({} :value (:card-name state) :on-change (connect :card-name))
