
(ns respo.schema )

(def component
  {:name nil,
   :respo-node :component,
   :coord nil,
   :args [],
   :render nil,
   :tree nil,
   :cost nil,
   :cursor nil})

(def effect
  {:name nil,
   :respo-node :effect,
   :coord [],
   :args [],
   :old-args [],
   :method (fn [args old-args params] )})

(def element
  {:name :div,
   :respo-node :element,
   :coord nil,
   :attrs nil,
   :style nil,
   :event nil,
   :children {}})
