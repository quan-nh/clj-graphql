(ns clj-graphql.system
  (:gen-class)
  (:require
    [clj-graphql.schema :as schema]
    [clj-graphql.server :as server]
    [com.stuartsierra.component :as component]))

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
