
Respo: A virtual DOM library in ClojureScript
----

> Inspired by React and Reagent.

* Docs http://respo.site
* Demo http://repo.respo.site/respo/
* Examples https://github.com/Respo/respo-examples
* Minimal App https://github.com/Respo/minimal-respo
* Quick Start https://github.com/mvc-works/respo/wiki/Quick-Start

### Usage

Respo is based on ClojureScript ecosystem. Use Boot or Leiningen to install it.

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.5.13"]
```

Component definition:

```clojure
(ns respo.comp.space
  (:rqeuire-macros [respo.macros :refer [defcomp div span <>]])
  (:require [respo.core :refer [create-comp create-element]]))

(defcomp comp-demo [content]
  (div
    {:class-name "demo-container"
     :style {:color :red}}
    (<> span content nil)))

; which expands to:
(def comp-demo
  (create-comp :demo
    (fn [content]
      (fn [cursor]
        (create-element :div
          {:class-name "demo-container"
           :style {:color :red}}
          (create-element :span {:inner-text content, :style nil}))))))
```

App initialization:

```clojure
(require '[respo.core :refer [render!]])

(defonce *store (atom {:point 0 :states {}}))

(defn dispatch! [op op-data]
  (reset! *store (updater @*store op op-data)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")
        app (comp-container @*store)]
    (render! target app dispatch!)))

(render-app!)
(add-watch *store :changes (fn [] (render-app!)))
```

### Test

```bash
yarn compile-test
node target/test.js
```

### Develop

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
