
# Respo

A responsive DOM library.

![](assets/respo.png)

## Installation

[![Respo](https://img.shields.io/clojars/v/mvc-works/respo.svg)](https://clojars.org/mvc-works/respo)

```clojure
[mvc-works/respo "0.1.22"]
```

```clojure
(respo.controller.deliver/build-deliver-event virtual-element-ref dispatch states-ref)
(def build-mutate (respo.controller.deliver/mutate-factory element-ref states-ref))
(respo.controller.resolver/get-element-at element coord)
(respo.render.expander/render-app element-markup global-states)
(respo.render.differ/find-element-diffs [] [] old-virtual-element virtual-element)
(respo.render.static-html/make-string virtual-element)
(respo.render.static-html/make-html virtual-element)
(respo.util.format/purify-element virtual-element)
(respo.alias/div {})
(respo.alias/create-comp :demo (fn [] (fn [state] (div))))
(respo.alias/create-element :demo props children)
(respo.component/debug/comp-debug data {})

(respo.controller.client/initialize-instance mount-point deliver-event)
(respo.controller.client/activate-instance virtual-element mount-point deliver-event)
(respo.controller.client/patch-instance changes mount-point deliver-event)
(respo.controller.client/release-instance mount-point)
```

## Component Definition

```clojure
(ns respo.component.space
  (:require [respo.alias :refer [create-comp div]]))

(defn style-space [w h]
  (if (some? w)
    {:width w, :display "inline-block", :height "1px"}
    {:width "1px", :display "inline-block", :height h}))

(defn render [w h] (fn [state mutate] (div {:style (style-space w h)})))

(def comp-space (create-comp :space render))
```

`mutable` is previously `set-state` but changed a lot.
Now you have to define `init-state` and `update-state` in every component.

## Develop

Clojure code is compiled from JSON from `cirru/` to `compiled/`...

Project workflow https://github.com/mvc-works/boot-workflow

```bash
boot dev
# open index.html
```

```bash
boot build-simple # without optimizations
boot build-advanced
```

```bash
boot watch-test
```

## Options

This project is mainly inspired by:

* React
* Reagent
* Deku

## License

MIT
