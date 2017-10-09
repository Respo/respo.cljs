
Respo: A virtual DOM library in ClojureScript
----

> Inspired by React and Reagent.

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.6.4"]
```

* Home http://respo.site
* [Bundled example](http://repo.respo.site/respo/)
* [Docs](https://github.com/Respo/respo/wiki)

### Usage

Component definition:

```clojure
(defcomp comp-container [content]
  (div
    {:class-name "demo-container"
     :style {:color :red}}
    (<> span content nil)))
```

App initialization:

```clojure
; initialize store and update store
(defonce *store (atom {:point 0 :states {}}))
(defn dispatch! [op op-data] (reset! *store updated-store))

; render to the DOM
(defn render-app! [] (render! mount-point (comp-container @*store) dispatch!))
(render-app!)

; watch store changes and render again
(add-watch *store :changes (fn [] (render-app!)))
```

Reset virtual DOM caching during hot code swapping, and rerender:

```clojure
(defn reload! []
  (clear-cache!)
  (render-app!))
```

Read docs to use Respo:

* [Beginner Guide](https://github.com/Respo/respo/wiki/Beginner-Guide)
* [Minimal App](https://github.com/Respo/minimal-respo)
* [Examples](https://github.com/Respo/respo-examples)

### Test

```bash
yarn compile-test
node target/test.js
```

### Develop

CoWorkflow https://github.com/mvc-works/coworkflow

### License

MIT
