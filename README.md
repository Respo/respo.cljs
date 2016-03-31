
# Respo

A responsive DOM library.

![](assets/respo.png)

## Installation

[![Respo](https://img.shields.io/clojars/v/mvc-works/respo.svg)](https://clojars.org/mvc-works/respo)

```clojure
[mvc-works/respo "0.1.8"]
```

```clojure
(respo.controller.deliver/build-deliver-event virtual-element-ref dispatch states-ref)
(respo.controller.deliver/do-states-gc states-ref virtual-element)
(respo.controller.resolver/get-element-at element coord)
(respo.renderer.expander/render-app element-markup global-states)
(respo.renderer.differ/find-element-diffs [] [] old-virtual-element virtual-element)
(respo.util.format/purify-element virtual-element)
```

## Component Definition

Imagine this is ClojureScript code:

```cirru
defn handle-event (data)
  fn (simple-event dispatch mutate)
    dispatch :op ({} :data :op-data)
    mutate :para1 :para2

def demo-component $ {}
  :name :demo

  :update-state $ fn (old-state para1 para2)
    merge old-state para1 para2

  :get-state $ fn (prop1 prop2)
    {}

  :render $ fn (prop1 prop2)
    fn (state)
      [] :div ({} :on-click (handle-event data))
        [] :div ({})
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
