
ns respo.update.core $ :require $ [] clojure.string :as string

defn update-transform
  old-store op-type op-data op-id
  .log js/console old-store op-type op-data
  case op-type
    :add $ conj old-store $ {} :text op-data :id op-id
    :remove $ ->> old-store
      filter $ fn (task)
        not $ = (:id task)
          , op-data

      into $ []

    :clear $ []
    :update $ let
      (task-id $ :id op-data)
        text $ :text op-data
      ->> old-store
        map $ fn (task)
          if
            = (:id task)
              , task-id
            assoc task :text text
            , task

        into $ []

    , old-store
