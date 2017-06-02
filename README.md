
Respo
----

> A virtual DOM library built with ClojureScript, inspired by React and Reagent.

* Docs http://respo.site
* Demo http://repo.respo.site/respo/
* Examples https://github.com/Respo/respo-examples
* Minimal App https://github.com/Respo/minimal-respo
* Quick Start https://github.com/mvc-works/respo/wiki/Quick-Start

### Usage

Respo is based on ClojureScript ecosystem. Use Boot or Leiningen to install it.

[![Respo](https://img.shields.io/clojars/v/respo/respo.svg)](https://clojars.org/respo/respo)

```clojure
[respo "0.4.3"]
```

Component definition:

```clojure
(ns respo.comp.space
  (:require [respo.alias :refer [create-comp div]]
            [respo.comp.text :refer [comp-text]]))

(def comp-demo
  (create-comp :demo
    (fn [content]
      (fn [cursor]
        (div
          {:class-name "demo-container"
           :style {:color :red}}
          (comp-text content nil))))))
```

App initialization:

```clojure
(require '[respo.core :refer [render!]])

(defonce ref-store (atom {:point 0 :states {}}))

(defn dispatch! [op op-data]
  (reset! ref-store (updater @ref-store op op-data)))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @ref-store) target dispatch!)))

(render-app!)
```

### Develop

Workflow(powered by Stack Editor) https://github.com/mvc-works/stack-workflow

To test:

```bash
export deps=`boot show -c`
lumo -Kc $deps:src/ -i test/html_test.cljs
```

### License

MIT
