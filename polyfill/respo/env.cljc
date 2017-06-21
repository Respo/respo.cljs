
(ns respo.env)

(def element-type #?(:clj clojure.lang.IType
                     ; in Clojure, just make sure it's false!!
                     :cljs (if (exists? js/Element) js/Element js/Error)))

(defn data->native [x] #?(:clj (pr-str x)
                          :cljs (clj->js x)))
