
(ns respo.util.detect)

(defn component? [x] (contains? x :tree))

(defn element? [x] (contains? x :event))

(defn =vector [a b]
  (if (not= (count a) (count b))
    false
    (loop [ax a bx b]
      (if (identical? (count ax) 0)
        true
        (if (identical? (get ax 0) (get bx 0))
          (recur (subvec ax 1) (subvec bx 1))
          false)))))

(defonce ctx
 (if (exists? js/document)
   (.getContext (.createElement js/document "canvas") "2d")
   nil))

(defn text-width [content font-size font-family]
  (if (some? ctx)
    (do
      (set! (.-font ctx) (str font-size "px " font-family))
      (.-width (.measureText ctx content)))
    nil))
