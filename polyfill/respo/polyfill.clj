
(ns respo.polyfill)

(def read-string* read-string)

(defn document-create-element* [tag-name] {})

(defn io-get-time* [] (quot (System/currentTimeMillis) 1000))

(defn raise* [x] (throw (Exception. x)))

(defn text-width* [content font-size font-family] 0)
