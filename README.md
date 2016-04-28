
# Respo

A responsive DOM library.

![](assets/respo.png)

## Installation

[![Respo](https://img.shields.io/clojars/v/mvc-works/respo.svg)](https://clojars.org/mvc-works/respo)

```clojure
[mvc-works/respo "0.1.17"]
```

```clojure
(respo.controller.deliver/build-deliver-event virtual-element-ref dispatch states-ref)
(def build-mutate (respo.controller.deliver/mutate-factory element-ref states-ref))
(respo.controller.resolver/get-element-at element coord)
(respo.renderer.expander/render-app element-markup global-states)
(respo.renderer.differ/find-element-diffs [] [] old-virtual-element virtual-element)
(respo.util.format/purify-element virtual-element)
(respo.alias/div {})
(respo.alias/create-comp :demo (fn [] (fn [state] (div))))
```

## Component Definition

Imagine this is ClojureScript code:

```cirru
defn handle-event (data)
  fn (simple-event dispatch mutate)
    dispatch :op ({} :data :op-data)
    mutate :para1 :para2

defn init-state (prop1 prop2)
  {}

defn update-state (old-state para1 para2)
  merge old-state para1 para2

defn render (prop1 prop2)
  fn (state)
    div ({} :on-click (handle-event data))
      div ({})

def demo-component $ create-comp :demo init-state update-state render
```

`mutable` is previously `set-state` but changed a lot.
Now you have to define `update-state` and `get-state` in every component.

## Develop

```bash
boot dev

cd target
node main.js
```

```bash
boot build-simple

cd target
node main.js
```

## Options

This project is mainly inspired by:

* React
* Reagent
* Deku

## License

MIT
