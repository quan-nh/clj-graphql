(ns clj-graphql.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]))

(def data (edn/read-string (slurp (io/resource "data.edn"))))

(defn game-by-id
  [_ {:keys [id]} _]
  (->> (:games data)
       (filter #(= id (:id %)))
       first))

(defn member-by-id
  [_ {:keys [id]} _]
  (->> (:members data)
       (filter #(= id (:id %)))
       first))

(defn rating-summary
  [_ _ {game-id :id}]
  (let [ratings (->> (:ratings data)
                     (filter #(= game-id (:game_id %)))
                     (map :rating))
        n       (count ratings)]
    {:count   n
     :average (if (zero? n)
                0
                (/ (apply + ratings)
                   (float n)))}))

(defn member-ratings
  [_ _ {member-id :id}]
  (->> (:ratings data)
       (filter #(= member-id (:member_id %)))))

(defn game-rating->game
  [_ _ {:keys [game_id]}]
  (->> (:games data)
       (filter #(= game_id (:id %)))
       first))

(defn load-schema
  []
  (-> (io/resource "hello-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers {:query/game-by-id         game-by-id
                              :query/member-by-id       member-by-id
                              :BoardGame/rating-summary rating-summary
                              :GameRating/game          game-rating->game
                              :Member/ratings           member-ratings})
      schema/compile))

(defrecord SchemaProvider [schema]
  component/Lifecycle
  (start [this]
    (assoc this :schema (load-schema)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider [] (map->SchemaProvider {}))
