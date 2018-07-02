
(ns respo.util.format
  (:require [clojure.string :as string] [respo.util.detect :refer [component? element?]]))

(def path-data-dict
  {:M 2,
   :m 2,
   :L 2,
   :l 2,
   :T 2,
   :t 2,
   :H 1,
   :h 1,
   :V 1,
   :v 1,
   :C 6,
   :c 6,
   :A 7,
   :a 7,
   :S 4,
   :s 4,
   :Q 4,
   :q 4,
   :Z 0,
   :z 0})

(defn concat-path-data [acc xs]
  (if (empty? xs)
    acc
    (let [cursor (first xs), following (rest xs), len (get path-data-dict cursor)]
      (if (nil? len) (throw (js/Error. (str "Unknown command: " cursor))))
      (let [params (take len following), next-xs (drop len following)]
        (assert
         (and (= len (count params)) (every? number? params))
         (str cursor " takes " len " numbers"))
        (if (not (empty? next-xs))
          (assert
           (keyword? (first next-xs))
           (str "extra param " (first next-xs) " after " cursor)))
        (recur (str acc " " (name cursor) (string/join "," params)) next-xs)))))

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

(defn path-data [& xs] (subs (concat-path-data "" xs) 1))

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
