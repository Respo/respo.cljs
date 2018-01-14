
(ns respo.util.format
  (:require [clojure.string :as string] [respo.util.detect :refer [component? element?]]))

(defn dashed->camel
  ([x] (dashed->camel "" x false))
  ([acc piece promoted?]
   (if (= piece "")
     acc
     (let [cursor (get piece 0), piece-followed (subs piece 1)]
       (if (= cursor "-")
         (recur acc piece-followed true)
         (recur
          (str acc (if promoted? (string/upper-case cursor) cursor))
          piece-followed
          false))))))

(defn ensure-string [x] (cond (string? x) x (keyword? x) (name x) :else (str x)))

(defn event->edn [event]
  (comment .log js/console "simplify event:" event)
  (-> (case (.-type event)
        "click" {:type :click}
        "keydown" {:type :keydown, :key-code (.-keyCode event), :keycode (.-keyCode event)}
        "keyup" {:type :keyup, :key-code (.-keyCode event), :keycode (.-keyCode event)}
        "input" {:type :input, :value (aget (.-target event) "value")}
        "change" {:type :change, :value (aget (.-target event) "value")}
        "focus" {:type :focus}
        {:type (.-type event), :msg (str "Unhandled event: " (.-type event))})
      (assoc :original-event event)
      (assoc :event event)))

(defn event->prop [x] (str "on" (name x)))

(defn event->string [x] (subs (name x) 3))

(defn mute-element [element]
  (if (component? element)
    (update element :tree mute-element)
    (-> element
        (update :event (fn [events] (list)))
        (update
         :children
         (fn [children]
           (->> children (map (fn [entry] [(first entry) (mute-element (last entry))]))))))))

(defn prop->attr [x]
  (case x "class-name" "class" "tab-index" "tabindex" "read-only" "readonly" x))

(defn purify-events [events] (->> events keys (into #{})))

(defn purify-element [markup]
  (if (nil? markup)
    nil
    (if (component? markup)
      (recur (:tree markup))
      (into
       {}
       (-> markup
           (update :event purify-events)
           (update
            :children
            (fn [children]
              (->> children (map (fn [entry] [(first entry) (purify-element (last entry))]))))))))))

(defn text->html [x]
  (if (some? x)
    (-> (str x)
        (string/replace (re-pattern ">") "&gt;")
        (string/replace (re-pattern "<") "&lt;"))
    nil))
