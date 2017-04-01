
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
[respo "0.4.2"]
```

```clojure
(require '[respo.core :refer [render!]])

(defonce ref-store (atom 0))

(defn dispatch! [op op-data]
  (reset! ref-store (updater @ref-store op op-data)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @ref-store) target dispatch!)))

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

(defn render [w h] (fn [cursor] (div {:style (style-space w h)})))

(def comp-space (create-comp :space render))
```

### Develop

Workflow https://github.com/mvc-works/stack-workflow

Test:

```bash
lumo -Kc $boot_deps:src/ -i test/html_test.cljs
```

### License

MIT
