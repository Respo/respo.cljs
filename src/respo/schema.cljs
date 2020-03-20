
(ns respo.schema )

(def component
  {:name nil,
   :respo-node :component,
   :coord nil,
   :args [],
   :render nil,
   :effects [],
   :tree nil,
   :local nil})

(def effect
  {:name nil,
   :respo-node :effect,
   :coord [],
   :args [],
   :method (fn [args [action parent *local]] )})

(def element
  {:name :div,
   :respo-node :element,
   :coord nil,
   :attrs nil,
   :style nil,
   :event nil,
   :children {}})
