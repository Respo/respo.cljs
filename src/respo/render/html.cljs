
(ns respo.render.html
  (:require [clojure.string :as string]
            [respo.util.format
             :refer
             [prop->attr
              purify-element
              mute-element
              ensure-string
              text->html
              get-style-value]]
            [respo.util.detect :refer [component? element?]]))

(defn escape-html [text]
  (if (nil? text)
    ""
    (-> text
        (string/replace (re-pattern "\"") "&quot;")
        (string/replace (re-pattern "<") "&lt;")
        (string/replace (re-pattern ">") "&gt;")
        (string/replace (re-pattern "\\n") "&#13;&#10;"))))

(defn style->string [styles]
  (->> styles
       (map
        (fn [entry]
          (let [k (first entry)
                style-name (name k)
                v (get-style-value (last entry) style-name)]
            (str style-name ":" (escape-html v) ";"))))
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
        (fn [[k v]] (and (some? v) (not (re-matches (re-pattern "^:on-.+") (str k))))))
       (map entry->string)
       (string/join " ")))

(defn element->string [element]
  (let [tag-name (name (:name element))
        attrs (into {} (:attrs element))
        styles (or (:style element) {})
        text-inside (if (= (:name element) :textarea)
                      (escape-html (:value attrs))
                      (or (:innerHTML attrs) (text->html (:inner-text attrs))))
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

(defn make-string [tree] (element->string (purify-element (mute-element tree))))
