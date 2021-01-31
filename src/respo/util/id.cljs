
(ns respo.util.id )

(def *cached-id (atom 0))

(defn get-id! [] (swap! *cached-id inc) (str "id-" @*cached-id))
