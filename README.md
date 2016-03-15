
# Respo

A responsive DOM library.

![](resources/public/images/respo.png)

## Installation

[![Respo](https://img.shields.io/clojars/v/mvc-works/respo.svg)](https://clojars.org/mvc-works/respo)

```clojure
[mvc-works/respo "0.1.1"]
```

```clojure
(respo.controller.deliver/build-deliver-event
  virtual-element-ref store-ref states-ref updater-method callback-method)
(respo.controller.deliver/do-states-gc states-ref virtual-element)
(respo.renderer.expander/render-app element-markup global-states)
(respo.renderer.differ/find-element-diffs [] [] old-virtual-element virtual-element)
(respo.util.format/purify-element virtual-element)
```

## Options

This project is mainly inspired by React and Reagent.

* Reagent

## License

Copyright © 2016 jiyinyiyong

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
