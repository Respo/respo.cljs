
Respo: A virtual DOM library in ClojureScript
----

> Inspired by React and Reagent.

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.13.0-a2"]
```

* Home http://respo-mvc.org
* [Bundled example](http://repo.respo-mvc.org/respo/)
* [Docs](https://github.com/Respo/respo/wiki)

### Usage

DOM syntax

```clojure
(div {:class-name "demo-container"
      :style {:color :red}
      :on-click (fn [event dispatch!])}
      (div {}))
```

Text Node:

```clojure
(<> content)
; with styles
(<> content {:color :red
             :font-size 14})
```

Component definition:

```clojure
(defcomp comp-container [content]
  (div
    {:class-name "demo-container"
     :style {:color :red}}
    (<> content)))
```

App initialization:

```clojure
; initialize store and update store
(defonce *store (atom {:point 0 :states {}}))
(defn dispatch! [op op-data] (reset! *store updated-store))

; render to the DOM
(render! mount-point (comp-container @*store) dispatch!)
```

Rerender on store changes:

```clojure
(defn render-app! [] (render! mount-point (comp-container @*store) dispatch!))

(add-watch *store :changes (fn [] (render-app!)))
```

Reset virtual DOM caching during hot code swapping, and rerender:

```clojure
(defn reload! []
  (clear-cache!)
  (render-app!))
```

Adding effects to component:

```clojure
(defeffect effect-a [text] [action parent-element at-place?]
  (println action) ; action could be :mount :update :amount
  (when (= :mount action)
    (do)))

(defcomp comp-a [text]
  [(effect-a text) (div {})])
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

Calcit Workflow https://github.com/mvc-works/calcit-workflow

### License

MIT
