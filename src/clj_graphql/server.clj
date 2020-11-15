(ns clj-graphql.server
  (:require [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal2 :as p2]
            [io.pedestal.http :as http]))

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
