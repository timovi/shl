(ns shl.service.game
  (require [clojure.java.jdbc :as j]
           [clojure.java.jdbc.sql :as sql]
           [honeysql.core :as s]
           [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.service.db :as db]))



(defn get-conference-standings [conferenceid]
  true)

(defn get-tournament-standings [tournamentid]
  true)