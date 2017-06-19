
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
[respo "0.4.5"]
```

Component definition:

```clojure
(ns respo.comp.space
  (:rqeuire-macros [respo.macros :refer [defcomp div]])
  (:require [respo.alias :refer [create-comp]]
            [respo.comp.text :refer [comp-text]]))

(defcomp comp-demo [content]
  (div
    {:class-name "demo-container"
     :style {:color :red}}
    (comp-text content nil)))

; ; which is expanded to:
; (def comp-demo
;   (create-comp :demo
;     (fn [content]
;       (fn [cursor]
;         (div
;           {:class-name "demo-container"
;            :style {:color :red}}
;           (comp-text content nil))))))
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
    (render! app target dispatch!)))

(render-app!)
(add-watch *store :changes (fn [] (render-app!)))
```

### Test

```bash
export deps=`boot show -c`
lumo -Kc $deps:src/:polyfill/ -i test/html_test.cljs
```

### Develop

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
