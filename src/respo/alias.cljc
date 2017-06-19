
(ns respo.alias (:require [respo.util.detect :refer [component? element?]]))

(defn val-exists? [pair] (some? (last pair)))

(defn arrange-children [children]
  (->> (if (and (= 1 (count children))
                (not (component? (first children)))
                (not (element? (first children))))
         (first children)
         (map-indexed vector children))
       (filter val-exists?)))

(defn pick-attrs [props]
  (if (nil? props)
    (list)
    (let [base-attrs (merge
                      (-> props
                          (dissoc :attrs)
                          (dissoc :event)
                          (dissoc :style)
                          (merge (:attrs props))))]
      (sort-by first base-attrs))))

(defn create-element [tag-name props & children]
  (let [attrs (pick-attrs props)
        styles (if (contains? props :style) (sort-by first (:style props)) (list))
        event (if (contains? props :event) (:event props) {})
        children (arrange-children children)]
    {:name tag-name,
     :coord nil,
     :attrs attrs,
     :style styles,
     :event event,
     :children children}))

(defn create-comp [comp-name render]
  (comment println "create component:" comp-name)
  (let [initial-comp {:name comp-name,
                      :coord nil,
                      :args [],
                      :render render,
                      :tree nil,
                      :cost nil,
                      :cursor nil}]
    (fn [& args] (assoc initial-comp :args args))))
