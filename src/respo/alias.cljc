
(ns respo.alias (:require [respo.util.detect :refer [component? element?]]))

(defn arrange-children [children]
  (->> (if (and (= 1 (count children))
                (not (component? (first children)))
                (not (element? (first children))))
         (first children)
         (map-indexed vector children))
       (filter (fn [pair] (some? (last pair))))))

(defn create-element [tag-name props children]
  (let [attrs (if (contains? props :attrs) (sort-by first (:attrs props)) (list))
        styles (if (contains? props :style) (sort-by first (:style props)) (list))
        event (if (contains? props :event) (:event props) {})
        children (arrange-children children)]
    {:coord nil,
     :children children,
     :name tag-name,
     :style styles,
     :event event,
     :attrs attrs}))

(defn canvas [props & children] (create-element :canvas props children))

(defn br [props & children] (create-element :br props children))

(defn img [props & children] (create-element :img props children))

(defn body [props & children] (create-element :body props children))

(defn p [props & children] (create-element :p props children))

(defn option [props & children] (create-element :option props children))

(defn footer [props & children] (create-element :footer props children))

(defn h2 [props & children] (create-element :h2 props children))

(def default-update merge)

(defn default-init [& args] {})

(defn create-comp
  ([comp-name render] (create-comp comp-name default-init default-update render))
  ([comp-name init-state update-state render]
   (comment println "create component:" comp-name)
   (let [initial-comp {:args [],
                       :coord nil,
                       :tree nil,
                       :name comp-name,
                       :init-state init-state,
                       :render render,
                       :cost nil,
                       :update-state update-state}]
     (fn [& args] (assoc initial-comp :args args)))))

(defn hr [props & children] (create-element :hr props children))

(defn style [props & children] (create-element :style props children))

(defn section [props & children] (create-element :section props children))

(defn span [props & children] (create-element :span props children))

(defn script [props & children] (create-element :script props children))

(defn select [props & children] (create-element :select props children))

(defn a [props & children] (create-element :a props children))

(defn meta' [props & children] (create-element :meta props children))

(defn input [props & children] (create-element :input props children))

(defn head [props & children] (create-element :head props children))

(defn title [props & children] (create-element :title props children))

(defn textarea [props & children] (create-element :textarea props children))

(defn link [props & children] (create-element :link props children))

(defn div [props & children] (create-element :div props children))

(defn pre [props & children] (create-element :pre props children))

(defn html [props & children] (create-element :html props children))

(defn h1 [props & children] (create-element :h1 props children))

(defn code [props & children] (create-element :code props children))

(defn header [props & children] (create-element :header props children))

(defn button [props & children] (create-element :button props children))
