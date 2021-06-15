
(ns respo.util.format
  (:require [clojure.string :as string] [respo.util.detect :refer [component? element?]]))

(def dashed-letter-pattern (new js/RegExp "-[a-z]" "g"))

(defn dashed->camel [x]
  (.replace x dashed-letter-pattern (fn [cc pos prop] (.toUpperCase (aget cc 1)))))

(defn ensure-string [x] (cond (string? x) x (keyword? x) (name x) :else (str x)))

(defn map-keyboard-event [event]
  {:key (.-key event),
   :code (.-code event),
   :ctrl? (.-ctrlKey event),
   :meta? (.-metaKey event),
   :alt? (.-altKey event),
   :shift? (.-shiftKey event)})

(defn event->edn [event]
  (comment .log js/console "simplify event:" event)
  (-> (case (.-type event)
        "click" {:type :click}
        "keydown"
          (merge
           (map-keyboard-event event)
           {:type :keydown, :key-code (.-keyCode event), :keycode (.-keyCode event)})
        "keypress" (merge (map-keyboard-event event) {:type :keypress})
        "keyup" (merge (map-keyboard-event event) {:type :keyup})
        "input"
          {:type :input,
           :value (aget (.-target event) "value"),
           :checked (.. event -target -checked)}
        "change" {:type :change, :value (aget (.-target event) "value")}
        "focus" {:type :focus}
        {:type (.-type event), :msg (str "Unhandled event: " (.-type event))})
      (assoc :original-event event)
      (assoc :event event)))

(defn event->prop [x] (str "on" (name x)))

(defn event->string [x] (subs (name x) 3))

(def pattern-non-dimensional
  (new js/RegExp "acit|ex(?:s|g|n|p|$)|rph|grid|ows|mnc|ntw|ine[ch]|zoo|^ord|itera" "i"))

(defn get-style-value [x style-name]
  (cond
    (string? x) x
    (keyword? x) (name x)
    (number? x) (if (.test pattern-non-dimensional style-name) (str x) (str x "px"))
    :else (str x)))

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
  (when (string/includes? x "?") (println "[Respo] warning: property contains `?` in" x))
  (case x "class-name" "class" "tab-index" "tabindex" "read-only" "readonly" x))

(defn purify-events [events] (->> events (filter (fn [[k v]] (some? v))) keys (into #{})))

(defn purify-element [markup]
  (cond
    (nil? markup) nil
    (component? markup) (recur (:tree markup))
    (element? markup)
      (into
       {}
       (-> markup
           (update :event purify-events)
           (update
            :children
            (fn [children] (->> children (map (fn [[k child]] [k (purify-element child)])))))))
    :else (do (js/console.warn "Unknown markup during purify:" markup) nil)))

(defn text->html [x]
  (if (some? x)
    (-> (str x)
        (string/replace (re-pattern ">") "&gt;")
        (string/replace (re-pattern "<") "&lt;"))
    nil))
