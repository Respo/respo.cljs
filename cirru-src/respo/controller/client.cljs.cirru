
ns respo.controller.client $ :require
  [] respo.renderer.patcher :refer $ [] apply-dom-changes
  [] cljs.reader :refer $ [] read-string
  [] respo.util.time :refer $ [] io-get-time
  [] respo.util.format :refer $ [] event->string event->edn
  [] respo.renderer.make-dom :refer $ [] make-element

defonce dom-registry $ atom $ {}

def no-bubble-events $ [] :on-scroll :on-focus :on-blur

def bubble-events $ [] :on-click :on-input :on-wheel :on-keydown :on-dbclick :on-change

defn read-coord (event)
  read-string $ ->> event (.-target)
    .-dataset
    .-coord

defn build-listener (event-name deliver-event)
  fn (event)
    let
      (coord $ read-coord event)
      deliver-event coord event-name $ event->edn event

defn activate-instance (entire-dom mount-point deliver-event)
  let
    (no-bubble-collection $ ->> no-bubble-events (map $ fn (event-name) ([] event-name $ build-listener event-name deliver-event)) (into $ {}))

    set! (.-innerHTML mount-point)
      , |
    .appendChild mount-point $ make-element entire-dom

defn patch-instance (changes mount-point deliver-event)
  let
    (no-bubble-collection $ ->> no-bubble-events (map $ fn (event-name) ([] event-name $ build-listener event-name deliver-event)) (into $ {}))

    apply-dom-changes changes mount-point

defn initialize-instance (mount-point deliver-event)
  let
    (bubble-collection $ ->> bubble-events (map $ fn (event-name) ([] event-name $ build-listener event-name deliver-event)) (into $ {}))

    doall $ ->> bubble-collection $ map $ fn (entry)
      let
        (event-string $ event->string $ name $ key entry)
          listener $ val entry
        .addEventListener mount-point event-string listener

    swap! dom-registry assoc mount-point $ {} $ :listeners bubble-collection

defn release-instance (mount-point)
  set! (.-innerHTML mount-point)
    , |
  doall $ ->>
    :listeners $ get @dom-registry mount-point
    map $ fn (entry)
      let
        (event-string $ event->string $ key entry)
          listener $ key entry
        .removeEventListener mount-point event-string listener

  swap! dom-registry dissoc mount-point
