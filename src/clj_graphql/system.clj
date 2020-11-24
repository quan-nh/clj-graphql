(ns clj-graphql.system
  (:gen-class)
  (:require
    [clj-graphql.schema :as schema]
    [clj-graphql.server :as server]
    [com.stuartsierra.component :as component]
    [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource)))

(def ^:private db-spec {:dbtype "postgres" :dbname "test_db" :host "localhost" :port 25432 :username "test_user" :password "pwd"})

(defn new-system
  []
  (-> (component/system-map
        :db (connection/component HikariDataSource db-spec)
        :schema-provider (schema/new-schema-provider)
        :server (server/new-server))

      (component/system-using
        {:server          [:schema-provider]
         :schema-provider [:db]})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (component/start (new-system)))
