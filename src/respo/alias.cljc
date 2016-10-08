
(ns respo.alias)

(defrecord
  Component
  [name coord args init-state update-state render tree cost])

(declare create-element)

(defrecord Element [name coord attrs style event children])

(defn style [props & children] (create-element :style props children))

(defn arrange-children [children]
  (->>
    (if (and
          (= 1 (count children))
          (not= Element (type (first children)))
          (not= Component (type (first children))))
      (first children)
      (map-indexed vector children))
    (into [])
    (filterv (fn [pair] (some? (last pair))))))

(defn create-element [tag-name props children]
  (let [attrs (if (contains? props :attrs)
                (into [] (sort-by first (:attrs props)))
                [])
        style (if (contains? props :style)
                (into [] (sort-by first (:style props)))
                [])
        event (if (contains? props :event) (:event props) {})
        children-map (arrange-children children)]
    (->Element tag-name nil attrs style event children-map)))

(defn canvas [props & children] (create-element :canvas props children))

(defn br [props & children] (create-element :br props children))

(defn img [props & children] (create-element :img props children))

(defn body [props & children] (create-element :body props children))

(defn p [props & children] (create-element :p props children))

(defn footer [props & children] (create-element :footer props children))

(defn h2 [props & children] (create-element :h2 props children))

(def default-update merge)

(defn default-init [& args] {})

(defn create-comp
  ([comp-name render]
    (create-comp comp-name default-init default-update render))
  ([comp-name init-state update-state render]
    (comment println "create component:" comp-name)
    (let [initial-comp (->Component
                         comp-name
                         nil
                         []
                         init-state
                         update-state
                         render
                         nil
                         nil)]
      (fn [& args] (assoc initial-comp :args (into [] args))))))

(defn hr [props & children] (create-element :hr props children))

(defn section [props & children]
  (create-element :section props children))

(defn span [props & children] (create-element :span props children))

(defn script [props & children] (create-element :script props children))

(defn a [props & children] (create-element :a props children))

(defn meta' [props & children] (create-element :meta props children))

(defn input [props & children] (create-element :input props children))

(defn head [props & children] (create-element :head props children))

(defn title [props & children] (create-element :title props children))

(defn textarea [props & children]
  (create-element :textarea props children))

(defn link [props & children] (create-element :link props children))

(defn div [props & children] (create-element :div props children))

(defn pre [props & children] (create-element :pre props children))

(defn html [props & children] (create-element :html props children))

(defn h1 [props & children] (create-element :h1 props children))

(defn code [props & children] (create-element :code props children))

(defn header [props & children] (create-element :header props children))

(defn button [props & children] (create-element :button props children))
