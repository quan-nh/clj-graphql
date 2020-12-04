(ns clj-graphql.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
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

(defn rate-game
  [db]
  (fn [_ args _]
    (let [{game-id   :game_id
           member-id :member_id
           rating    :rating} args
          game   (db/find-game-by-id db game-id)
          member (db/find-member-by-id db member-id)]
      (cond
        (nil? game)
        (resolve-as nil {:message "Game not found."
                         :status  404})

        (nil? member)
        (resolve-as nil {:message "Member not found."
                         :status  404})

        (not (<= 1 rating 5))
        (resolve-as nil {:message "Rating must be between 1 and 5."
                         :status  400})

        :else
        (do
          (db/upsert-game-rating db game-id member-id rating)
          game)))))

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

(def *ping-subscribes (atom 0))
(def *ping-cleanups (atom 0))

(defn stream-ping
  [context {:keys [message count]} source-stream]
  (swap! *ping-subscribes inc)
  (let [runnable ^Runnable (fn []
                             (dotimes [i count]
                               (Thread/sleep 500)
                               (source-stream {:message   (str message " #" (inc i))
                                               :timestamp (System/currentTimeMillis)}))

                             (source-stream nil))]
    (.start (Thread. runnable "stream-ping-thread")))
  ;; Return a cleanup fn
  #(swap! *ping-cleanups inc))

(defn load-schema
  [db]
  (-> (io/resource "hello-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers {:query/game-by-id         (game-by-id db)
                              :query/member-by-id       (member-by-id db)
                              :mutation/rate-game       (rate-game db)
                              :BoardGame/rating-summary (rating-summary db)
                              :GameRating/game          (game-rating->game db)
                              :Member/ratings           (member-ratings db)})
      (util/attach-streamers {:stream-ping stream-ping})
      schema/compile))

(defrecord SchemaProvider [schema db]
  component/Lifecycle
  (start [this]
    (let [db-opts (jdbc/with-options (db) {:builder-fn rs/as-unqualified-lower-maps})]
      (assoc this :schema (load-schema db-opts))))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider [] (map->SchemaProvider {}))
