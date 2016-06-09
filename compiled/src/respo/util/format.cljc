
(ns respo.util.format
  (:require [clojure.string :as string]
            [respo.util.detect :refer [component? element?]]))

(defn dashed->camel
  ([x] (dashed->camel "" x false))
  ([acc piece promoted?]
    (if (= piece "")
      acc
      (let [cursor (get piece 0) piece-followed (subs piece 1)]
        (if (= cursor "-")
          (recur acc piece-followed true)
          (recur
            (str acc (if promoted? (string/upper-case cursor) cursor))
            piece-followed
            false))))))

(defn prop->attr [x] (case x "class-name" "class" x))

(defn event->string [x] (subs (name x) 3))

(defn event->prop [x] (string/replace (name x) "-" ""))

(defn event->edn [event]
  (comment .log js/console "simplify event:" event)
  (case
    (.-type event)
    "click"
    {:type :click}
    "keydown"
    {:key-code (.-keyCode event), :type :keydown}
    "input"
    {:value (.-value (.-target event)), :type :input}
    {:msg "not recognized event", :type (.-type event)}))

(defn purify-events [events]
  (->> events (map (fn [entry] [(key entry) true])) (into {})))

(defn purify-element [markup]
  (if (nil? markup)
    nil
    (if (component? markup)
      (recur (:tree markup))
      (into
        {}
        (-> markup
         (assoc :event (purify-events (:event markup)))
         (assoc
           :children
           (->>
             (:children markup)
             (map
               (fn [entry] [(key entry)
                            (purify-element (val entry))])))))))))
