(ns clj-graphql.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]))

(defn ^:private resolve-hello
  [context args value]
  "Hello, Clojurians!")

(defn load-schema
  []
  (-> (io/resource "hello-schema.edn")
      slurp
      edn/read-string
      (util/inject-resolvers {:queries/hello resolve-hello})
      schema/compile))

(defrecord SchemaProvider [schema]
  component/Lifecycle
  (start [this]
    (assoc this :schema (load-schema)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider [] (map->SchemaProvider {}))
