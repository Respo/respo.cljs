
(ns respo.caches (:require [memof.core :as memof]))

(defonce *memof-caches (atom (memof/new-states {:trigger-loop 100, :elapse-loop 600})))
