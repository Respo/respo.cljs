
(ns respo.render.html
  (:require [clojure.string :as string]
            [respo.util.format
             :refer
             [prop->attr purify-element mute-element ensure-string text->html]]
            [respo.util.detect :refer [component? element?]]
            [respo.render.expand :refer [render-app]]))

(defn escape-html [text] (-> text (string/replace (re-pattern "\"") "&quot;")))

(defn style->string [styles]
  (->> styles
       (map
        (fn [entry]
          (let [k (first entry), v (last entry)]
            (str (name k) ":" (if (string? v) (escape-html v) (ensure-string v)) ";"))))
       (string/join "")))

(defn entry->string [entry]
  (let [k (first entry), v (last entry)]
    (str
     (prop->attr (name k))
     "="
     (pr-str
      (cond
        (= k :style) (style->string v)
        (boolean? v) (str v)
        (number? v) (str v)
        (keyword? v) (name v)
        (string? v) (escape-html v)
        :else (str v))))))

(defn props->string [props]
  (->> props
       (filter
        (fn [entry]
          (let [k (first entry)] (not (re-matches (re-pattern "^:on-.+") (str k))))))
       (map entry->string)
       (string/join " ")))

(defn element->string [element]
  (let [tag-name (name (:name element))
        attrs (into {} (:attrs element))
        styles (or (:style element) {})
        text-inside (or (:innerHTML attrs) (text->html (:inner-text attrs)))
        tailored-props (-> attrs
                           (dissoc :innerHTML)
                           (dissoc :inner-text)
                           ((fn [props]
                              (if (empty? styles) props (assoc props :style styles)))))
        props-in-string (props->string tailored-props)
        children (->> (:children element)
                      (map (fn [entry] (let [child (last entry)] (element->string child)))))]
    (str
     "<"
     tag-name
     (if (> (count props-in-string) 0) " " "")
     props-in-string
     ">"
     (or text-inside (string/join "" children))
     "</"
     tag-name
     ">")))

(defn make-string [tree]
  (let [element (render-app tree nil)]
    (element->string (purify-element (mute-element element)))))
