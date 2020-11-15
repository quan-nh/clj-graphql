## Component

add dep
```clojure
[com.stuartsierra/component "1.0.0"]
```

`server.clj`
```clojure
(defrecord Server [schema-provider server]
  component/Lifecycle
  (start [this]
    (if server
      this
      (assoc this :server (-> schema-provider
                              :schema
                              (p2/default-service nil)
                              http/create-server
                              http/start))))

  (stop [this]
    (when server (http/stop server))
    (assoc this :server nil)))

(defn new-server [] (map->Server {}))
```

`schema.clj`
```clojure
(defrecord SchemaProvider [schema]
  Lifecycle
  (start [this]
    (assoc this :schema (load-schema)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider [] (map->SchemaProvider {}))
```

rename `core.clj` to `system.clj`
```clojure
(defn new-system
  []
  (-> (component/system-map
        :schema-provider (schema/new-schema-provider)
        :server (server/new-server))

      (component/system-using
        {:server [:schema-provider]})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (component/start (new-system)))
```

`lein run`

## Reloadable Workflow

add dev profile
```clojure
:profiles {:dev     {:source-paths ["dev"]
                     :dependencies [[com.stuartsierra/component.repl "0.2.0"]]}
           ..}
:repl-options {:init-ns user}
```

`dev/user.clj`
```clojure
(ns user
  (:require
    [clj-graphql.system :refer [new-system]]
    [com.stuartsierra.component.repl
     :refer [reset set-init start stop system]]))

(set-init (constantly (new-system)))
```

`lein repl`

You can now manipulate the system in the REPL: `(start)`, `(stop)` & `(reset)`

Change the code & `(reset)` to see if it affects!
