
(ns respo.util.format
  (:require [clojure.string :as string] [respo.util.detect :refer [component? element?]]))

(defn concat-path-data [acc xs]
  (if (empty? xs)
    acc
    (let [cursor (first xs), following (rest xs)]
      (cond
        (contains? #{:M :m :L :l :T :t} cursor)
          (let [params (take 2 following)]
            (assert
             (and (= 2 (count params)) (every? number? params))
             (str cursor " takes 2 numbers"))
            (recur (str acc " " (name cursor) (string/join "," params)) (drop 2 following)))
        (contains? #{:H :h :V :v} cursor)
          (let [params (take 1 following)]
            (assert
             (and (= 1 (count params)) (every? number? params))
             (str cursor " takes 1 numbers"))
            (recur (str acc " " (name cursor) (string/join "," params)) (drop 1 following)))
        (contains? #{:C :c} cursor)
          (let [params (take 6 following)]
            (assert
             (and (= 6 (count params)) (every? number? params))
             (str cursor " takes 6 numbers"))
            (recur (str acc " " (name cursor) (string/join "," params)) (drop 6 following)))
        (contains? #{:A :a} cursor)
          (let [params (take 7 following)]
            (assert
             (and (= 7 (count params)) (every? number? params))
             (str cursor " takes 6 numbers"))
            (recur (str acc " " (name cursor) (string/join "," params)) (drop 7 following)))
        (contains? #{:S :s :Q :q} cursor)
          (let [params (take 4 following)]
            (assert
             (and (= 4 (count params)) (every? number? params))
             (str cursor " takes 4 numbers"))
            (recur (str acc " " (name cursor) (string/join "," params)) (drop 4 following)))
        (contains? #{:Z :z} cursor) (recur (str acc " Z") following)
        :else (throw (js/Error. (str "Unknown command: " cursor)))))))

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
