
(ns respo.env)

(def element-type (if (exists? js/Element) js/Element js/Error))

(defn data->native [x] :cljs (clj->js x))
