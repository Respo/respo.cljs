
(ns respo.util.dom )

(def shared-canvas-context
  (if (and (exists? js/window) (exists? js/document))
    (.getContext (.createElement js/document "canvas") "2d")
    nil))

(defn text-width [content font-size font-family]
  (if (some? shared-canvas-context)
    (do
     (set! (.-font shared-canvas-context) (str font-size "px " font-family))
     (.-width (.measureText shared-canvas-context content)))
    nil))
