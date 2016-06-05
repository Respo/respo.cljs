
(ns respo.websocket
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :as a :refer [>! <! chan]]
            [cljs.reader :as reader]))

(defonce receive-chan (chan))

(defonce send-chan (chan))

(defonce shortid (js/require "shortid"))

(defonce ws (js/require "ws"))

(defonce WebSocketServer (.-Server ws))

(defonce socket-registry (atom {}))

(def wss (new WebSocketServer (js-obj "port" 4005)))

(.on
  wss
  "connection"
  (fn [socket]
    (let [state-id (.generate shortid) now (new js/Date)]
      (println "new socket" state-id)
      (go
        (>!
          receive-chan
          {:meta
           {:time (.valueOf now),
            :id (.generate shortid),
            :state-id state-id},
           :type :state/connect,
           :data nil}))
      (swap! socket-registry assoc state-id socket)
      (.on
        socket
        "message"
        (fn [rawData]
          (let [now (new js/Date) action (reader/read-string rawData)]
            (go
              (>!
                receive-chan
                {:meta
                 {:time (.valueOf now),
                  :id (.generate shortid),
                  :state-id state-id},
                 :type :event,
                 :data action})))))
      (.on
        socket
        "close"
        (fn []
          (let [now (new js/Date)]
            (swap! socket-registry dissoc state-id)
            (println "socket close" state-id)
            (go
              (>!
                receive-chan
                {:meta
                 {:time (.valueOf now),
                  :id (.generate shortid),
                  :state-id state-id},
                 :type :state/disconnect,
                 :data nil}))))))))

(go
  (loop []
    (let [msg-pack (<! send-chan)
          socket (get @socket-registry (first msg-pack))]
      (comment println "sending message pack:" (pr-str msg-pack))
      (if (some? socket)
        (.send socket (pr-str (last msg-pack)))
        (println
          "found not socket:"
          (first msg-pack)
          (pr-str @socket-registry)))
      (recur))))
