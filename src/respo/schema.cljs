
(ns respo.schema )

(def cache-info {:value nil, :initial-loop nil, :last-hit nil, :hit-times 0})

(def component {:name nil, :respo-node :component, :coord nil, :effects [], :tree nil})

(def effect
  {:name nil,
   :respo-node :effect,
   :coord [],
   :args [],
   :method (fn [args [action parent at-place?]] )})

(def element
  {:name :div,
   :respo-node :element,
   :coord nil,
   :attrs nil,
   :style nil,
   :event nil,
   :children {}})
