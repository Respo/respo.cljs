
(ns respo.env)

(def element-type #?(:clj clojure.lang.IType
                     ; in Clojure, just make sure it's false!!
                     :cljs js/Element))
