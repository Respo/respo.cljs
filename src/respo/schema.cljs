
(ns respo.schema )

(def component
  {:name nil,
   :kind :component,
   :coord nil,
   :args [],
   :render nil,
   :tree nil,
   :cost nil,
   :cursor nil})

(def effect {:name nil, :kind :effect, :coord [], :args [], :old-args [], :method (fn [] )})

(def element
  {:name :div, :kind :element, :coord nil, :attrs nil, :style nil, :event nil, :children {}})
