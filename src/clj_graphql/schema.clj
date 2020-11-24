(ns clj-graphql.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [clj-graphql.db :as db]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(defn game-by-id
  [db]
  (fn [_ args _]
    (db/find-game-by-id db (:id args))))

(defn member-by-id
  [db]
  (fn [_ args _]
    (db/find-member-by-id db (:id args))))

(defn rating-summary
  [db]
  (fn [_ _ game]
    (let [ratings (map :rating (db/list-ratings-for-game db (:id game)))
          n       (count ratings)]
      {:count   n
       :average (if (zero? n)
                  0
                  (/ (apply + ratings)
                     (float n)))})))

(defn member-ratings
  [db]
  (fn [_ _ member]
    (db/list-ratings-for-member db (:id member))))

(defn game-rating->game
  [db]
  (fn [_ _ game-rating]
    (db/find-game-by-id db (:game_id game-rating))))

(defn load-schema
  [db]
  (-> (io/resource "hello-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers {:query/game-by-id         (game-by-id db)
                              :query/member-by-id       (member-by-id db)
                              :BoardGame/rating-summary (rating-summary db)
                              :GameRating/game          (game-rating->game db)
                              :Member/ratings           (member-ratings db)})
      schema/compile))

(defrecord SchemaProvider [schema db]
  component/Lifecycle
  (start [this]
    (let [db-opts (jdbc/with-options (db) {:builder-fn rs/as-unqualified-lower-maps})]
      (assoc this :schema (load-schema db-opts))))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider [] (map->SchemaProvider {}))
