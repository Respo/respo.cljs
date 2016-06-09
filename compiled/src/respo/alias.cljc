
(ns respo.alias)

(defrecord Element [name coord c-coord attrs style event children])

(defrecord
  Component
  [name coord args init-state update-state render tree cost])

(defn arrange-children [children]
  (sort-by
    first
    (if (= 1 (count children))
      (let [cursor (first children)]
        (if (or (= Element (type cursor)) (= Component (type cursor)))
          (->> children (map-indexed vector))
          cursor))
      (->> children (map-indexed vector)))))

(defn create-element [tag-name props children]
  (let [attrs (if (contains? props :attrs)
                (sort-by first (:attrs props))
                (list))
        style (if (contains? props :style)
                (sort-by first (:style props))
                (list))
        event (if (contains? props :event) (:event props) (list))
        children-map (arrange-children children)]
    (->Element tag-name nil nil attrs style event children-map)))

(defn default-init [& args] {})

(def default-update merge)

(defn create-comp
  ([comp-name render]
    (create-comp comp-name default-init default-update render))
  ([comp-name init-state update-state render]
    (fn [& args]
      (->Component
        comp-name
        nil
        args
        init-state
        update-state
        render
        nil
        nil))))

(defn div [props & children]
  (let [attrs (:attrs props)]
    (if (contains? attrs :inner-text)
      (println "useing in div is dangerous!"))
    (create-element :div props children)))

(defn img [props & children] (create-element :img props children))

(defn span [props & children] (create-element :span props children))

(defn input [props & children] (create-element :input props children))

(defn button [props & children] (create-element :button props children))

(defn header [props & children] (create-element :header props children))

(defn section [props & children]
  (create-element :section props children))

(defn footer [props & children] (create-element :footer props children))

(defn textarea [props & children]
  (create-element :textarea props children))

(defn code [props & children] (create-element :code props children))

(defn pre [props & children] (create-element :pre props children))

(defn canvas [props & children] (create-element :canvas props children))

(defn html [props & children] (create-element :html props children))

(defn head [props & children] (create-element :head props children))

(defn body [props & children] (create-element :body props children))

(defn link [props & children] (create-element :link props children))

(defn meta' [props & children] (create-element :meta props children))

(defn script [props & children] (create-element :script props children))

(defn style [props & children] (create-element :style props children))

(defn title [props & children] (create-element :title props children))
