create app
```
lein new app clj-graphql
```

add `walmartlabs/lacinia-pedestal` deps
```clojure
[com.walmartlabs/lacinia-pedestal "1.1"]
[io.aviso/logging "1.0"]
```

start with a schema file, `resources/hello-schema.edn`
```clojure
{:queries
 {:hello
  {:type String}}}
```

edit `core.clj` file to load schema, add resolvers & compile it
```clojure
(ns clj-graphql.core
  (:gen-class)
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.pedestal2 :as p2]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.util :as util]
    [io.pedestal.http :as http]))

(defn ^:private resolve-hello
  [context args value]
  "Hello, Clojurians!")

(defn ^:private hello-schema
  []
  (-> (io/resource "hello-schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers {:queries/hello resolve-hello})
      schema/compile))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> (hello-schema)
      (p2/default-service nil)
      http/create-server
      http/start))
```

start app
```
lein run
```

The GraphQL endpoint will be at http://localhost:8888/api
and the GraphIQL client will be at http://localhost:8888/ide.

Input the `{hello}` query & run to see result.
