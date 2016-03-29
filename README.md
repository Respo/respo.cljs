
# Respo

A responsive DOM library.

![](resources/public/images/respo.png)

## Installation

[![Respo](https://img.shields.io/clojars/v/mvc-works/respo.svg)](https://clojars.org/mvc-works/respo)

```clojure
[mvc-works/respo "0.1.8"]
```

```clojure
(respo.controller.deliver/build-deliver-event virtual-element-ref intent states-ref)
(respo.controller.deliver/do-states-gc states-ref virtual-element)
(respo.controller.resolver/get-element-at element coord)
(respo.renderer.expander/render-app element-markup global-states)
(respo.renderer.differ/find-element-diffs [] [] old-virtual-element virtual-element)
(respo.util.format/purify-element virtual-element)
```

## Develop

```bash
boot dev
```

```
boot gen-static
```

## Options

This project is mainly inspired by:

* React
* Reagent
* Deku

## License

MIT
