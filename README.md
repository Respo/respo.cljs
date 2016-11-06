
Respo
----

A front-end MVC library in ClojureScript.

* Home http://respo.site
* Quick Start https://github.com/mvc-works/respo/wiki/Quick-Start
* Demo http://repo.respo.site/respo/

This project is inspired by:

* React
* Reagent
* Deku

### Usage

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.3.29"]
```

```clojure
(require '[respo.core :refer [render!]])

(defonce store-ref (atom 0))
(defonce states-ref (atom {}))

(defn dispatch! [op op-data]
  (reset! store-ref (updater @store-ref op op-data)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref) target dispatch! states-ref)))

(render-app!)
```

### Component Definition

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

`mutate!` is previously `set-state` but changed a lot.
Now you have to define `init-state` and `update-state` in every component.

### Develop

Workflow https://github.com/mvc-works/stack-workflow

```bash
boot dev!
# open target/dev.html
```

Edit code with Stack Editor http://repo.cirru.org/stack-editor

To test static HTML rendering:

```bash
boot watch-test
```

Clojure code can be compiled from `stack-sepal.ir` to `src/` by:

```bash
boot generate-code
```

To compile the whole project:

```bash
boot build-advanced
```

### License

MIT
