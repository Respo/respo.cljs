
ns respo.controller.deliver $ :require
  [] respo.controller.resolver :refer $ [] find-event-target get-markup-at
  [] respo.alias :refer $ [] Component Element

defn all-component-coords (markup)
  if
    = Component $ type markup
    cons (:coord markup)
      all-component-coords $ :tree markup
    ->> (:children markup)
      map $ fn (child-entry)
        all-component-coords $ val child-entry
      apply concat

defn purify-states (new-states old-states all-coords)
  if
    = (count old-states)
      , 0
    , new-states
    let
      (first-entry $ first old-states)
      recur
        if
          some
            fn (component-coord)
              = component-coord $ key first-entry
            , all-coords

          assoc new-states (key first-entry)
            val first-entry
          , new-states

        rest old-states
        , all-coords

defn do-states-gc (states-ref element)
  println "|states GC:" $ pr-str @states-ref
  let
    (all-coords $ distinct (all-component-coords element))
      new-states $ purify-states ({})
        , @states-ref all-coords

    println $ pr-str all-coords
    reset! states-ref new-states

defn build-deliver-event (element-ref dispatch)
  fn (coord event-name simple-event)
    let
      (target-element $ find-event-target @element-ref coord event-name)
        target-listener $ get (:event target-element)
          , event-name

      if (some? target-listener)
        do
          println "|listener found:" coord event-name
          target-listener simple-event dispatch
        println "|found no listener:" coord event-name
