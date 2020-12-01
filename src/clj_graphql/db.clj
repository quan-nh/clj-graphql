(ns clj-graphql.db
  (:require [next.jdbc :as jdbc]))

(defn find-game-by-id [db game-id]
  (jdbc/execute-one! db ["select id, name, description, designers, min_players, max_players, created_at, updated_at
               from board_game where id = ?" game-id]))

(defn find-member-by-id [db member-id]
  (jdbc/execute-one! db ["select id, name, created_at, updated_at
               from member where id = ?" member-id]))

(defn list-ratings-for-game [db game-id]
  (jdbc/execute! db ["select game_id, member_id, rating, created_at, updated_at
           from game_rating
           where game_id = ?" game-id]))

(defn list-ratings-for-member [db member-id]
  (jdbc/execute! db ["select game_id, member_id, rating, created_at, updated_at
           from game_rating
           where member_id = ?" member-id]))

(defn upsert-game-rating
  [db game-id member-id rating]
  (jdbc/execute! db
                 ["insert into game_rating (game_id, member_id, rating)
           values (?, ?, ?)
           on conflict (game_id, member_id) do update set rating = ?"
                  game-id member-id rating rating]))
