(ns user
  (:require
    [clj-graphql.system :refer [new-system]]
    [com.stuartsierra.component.repl
     :refer [reset set-init start stop system]]))

(set-init (constantly (new-system)))
