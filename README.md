
# Respo

A responsive DOM library.

Demo http://repo.tiye.me/mvc-works/respo/

[Quick Start](https://github.com/mvc-works/respo/wiki/Quick-Start)

## Usage

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.3.8"]
```

```clojure
(require '[respo.core :refer [render!]])

(defonce store-ref (atom 0))
(defonce states-ref (atom {}))

(defn dispatch! [op op-data]
  (reset! store-ref (updater @store-ref op op-data)))

(defn render-app []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref) target dispatch! states-ref)))

(render-app)
```

## Component Definition

```clojure
(ns respo.comp.space
  (:require [respo.alias :refer [create-comp div]]))

(defn style-space [w h]
  (if (some? w)
    {:width w, :display "inline-block", :height "1px"}
    {:width "1px", :display "inline-block", :height h}))

(defn render [w h] (fn [state mutate!] (div {:style (style-space w h)})))

(def comp-space (create-comp :space render))
```

`mutable` is previously `set-state` but changed a lot.
Now you have to define `init-state` and `update-state` in every component.

### Low level APIs

```clojure
(respo.alias/div {})
(respo.alias/create-comp :demo (fn [] (fn [state] (div))))
(respo.alias/create-element :demo props children)

(respo.comp.debug/comp-debug data style)
(respo.comp.space/comp-space w h)
(respo.comp.text/comp-text content style)

(respo.controller.client/initialize-instance mount-point deliver-event)
(respo.controller.client/activate-instance virtual-element mount-point deliver-event)
(respo.controller.client/patch-instance changes mount-point deliver-event)
(respo.controller.client/release-instance mount-point)

(respo.render.static-html/make-string virtual-element)
(respo.render.static-html/make-html virtual-element)

(respo.controller.deliver/build-deliver-event virtual-element-ref dispatch! states-ref)
(def build-mutate (respo.controller.deliver/mutate-factory element-ref states-ref))

(respo.controller.resolver/get-element-at element coord)

(respo.render.expander/render-app element-markup global-states)

(respo.render.differ/find-element-diffs [] [] old-virtual-element virtual-element)

(respo.util.format/purify-element virtual-element)
```

## Develop

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

Clojure code is compiled from JSON from `cirru/` to `compiled/` by:

```bash
boot compile-cirru
```

My way of programming is with my graphical editor, like doing this:

```bash
npm i -g cirru-light-editor
boot dev
cle cirru/
# open http://repo.cirru.org/light-editor/
# # try Command P, Command Shift P, Command S, Left, Right, Up, Down...
```

## Options

This project is inspired by:

* React
* Reagent
* Deku

## License

MIT
