
ns respo.update.core $ :require $ [] clojure.string :as string

defn update-transform
  old-store op-type op-data op-id
  .log js/console old-store op-type op-data
  case op-type
    :add $ conj old-store $ {} :text op-data :id op-id
    , old-store
