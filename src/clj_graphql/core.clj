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
