
(ns respo.caches (:require [clojure.string :as string]))

(defonce *cache-states
  (atom {:loop 0, :caches {}, :gc {:cold-duration 400, :trigger-loop 100, :elapse-loop 50}}))

(defn access-cache [params]
  (let [caches (@*cache-states :caches), the-loop (@*cache-states :loop)]
    (if (contains? caches params)
      (do
       (swap!
        *cache-states
        update-in
        [:caches params]
        (fn [info] (-> info (assoc :last-hit the-loop) (update :hit-times inc))))
       (:value (get caches params)))
      nil)))

(defn perform-gc! []
  (let [states-0 @*cache-states, gc (states-0 :gc)]
    (swap!
     *cache-states
     update
     :caches
     (fn [caches]
       (->> caches
            (remove
             (fn [[params info]]
               (cond
                 (zero? (info :hit-times)) true
                 (> (- (states-0 :loop) (info :hit-loop)) (gc :elapse-loop)) true
                 :else false)))
            (into {}))))
    (println
     "[Respo Caches] Performed GC, from "
     (count (states-0 :caches))
     " to "
     (count (@*cache-states :caches)))))

(defn new-loop! []
  (swap! *cache-states update :loop inc)
  (let [loop-count (@*cache-states :loop), gc (@*cache-states :gc)]
    (when (and (> loop-count (gc :cold-duration)) (zero? (rem loop-count (gc :trigger-loop))))
      (perform-gc!))))

(defn reset-caches! [] (swap! *cache-states assoc :loop 0 :caches {}))

(defn show-summary! []
  (println "Caches summary:")
  (doseq [[params info] (@*cache-states :caches)]
    (println "PARAMS:" params)
    (println "INFO:" (assoc info :value 'VALUE))))

(defn write-cache! [params value]
  (let [the-loop (@*cache-states :loop)]
    (swap!
     *cache-states
     update
     :caches
     (fn [caches]
       (if (contains? caches params)
         (do
          (println "[Respo Caches] already exisits" params)
          (update
           caches
           params
           (fn [info] (-> info (assoc :last-hit the-loop) (update :hit-times inc)))))
         (assoc
          caches
          params
          {:value value, :initial-loop the-loop, :last-hit the-loop, :hit-times 0}))))))

(defn user-scripts []
  (show-summary!)
  (write-cache! [1 2 3 4] 10)
  (write-cache! [1 2 3] 6)
  (access-cache [1 2 3 4])
  (new-loop!)
  (println @*cache-states)
  (js/console.clear))
