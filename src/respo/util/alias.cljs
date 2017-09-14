
(ns respo.util.alias (:require [clojure.string :as string]))

(defn parse-alias
  ([alias]
   (if (string/blank? alias) (throw (js/Error. "Alias should not be empty!")))
   (parse-alias alias :name "" {:name nil, :class-name nil, :id nil}))
  ([alias mode buffer detail]
   (let [end? (string/blank? alias)
         class-updater (fn [x] (if (nil? x) buffer (str x " " buffer)))]
     (comment println "calling" alias mode buffer detail)
     (if (and end?)
       (do
        (if (string/blank? buffer) (throw (js/Error. "Buffer is empty!")))
        (case mode
          :name (assoc detail :name (keyword buffer))
          :class-name (update detail :class-name class-updater)
          :id (assoc detail :id buffer)
          (throw (js/Error. (str "Unexpected mode: " mode)))))
       (let [x (subs alias 0 1)
             xs' (subs alias 1)
             next-detail (if (and (not (string/blank? buffer)) (or (= x ".") (= x "#")))
                           (case mode
                             :class-name (update detail :class-name class-updater)
                             :id (assoc detail :id buffer)
                             :name (assoc detail :name buffer)
                             (throw (js/Error. (str "Unexpected mode: " mode))))
                           detail)]
         (case x
           "." (recur xs' :class-name "" next-detail)
           "#" (recur xs' :id "" next-detail)
           (recur xs' mode (str buffer x) detail)))))))
