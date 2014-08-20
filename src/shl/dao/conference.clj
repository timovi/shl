(ns shl.dao.conference
  (require [clojure.java.jdbc :as j]
           [honeysql.core :as s]
           [clj-time.coerce :as time]
           [shl.utils.time :as time-utils]
           [shl.dao.db :as db]))

(defn add-tournament [name startdate enddate 
                      games-per-player playoff-teams-per-conference]
  (j/insert! db/db :tournament {:name name 
                                :startdate (time/to-sql-date startdate)
                                :enddate (time/to-sql-date enddate)
                                :gamesperplayer games-per-player
                                :playoffteamsperconference playoff-teams-per-conference
                                :active true}))

(defn add-conference [name tournamentid]
  (j/insert! db/db :conference {:name name 
                                :tournamentid tournamentid}))

(defn inactivate-tournaments []
  (j/update! db/db :tournament {:active false} ["active = ?" true]))

(defn- get-active-tournament-sql []
    (s/build :select :* 
             :from [[:tournament :t]] 
             :where [:= :t.active true]
             :order-by [[:t.id :desc]]))

(defn get-active-tournament []
  (first 
    (j/query db/db 
       (s/format (get-active-tournament-sql))
       :row-fn #(assoc % :startdate (str (time-utils/from-sql-date (% :startdate)))
                         :enddate (str (time-utils/from-sql-date (% :enddate))))
    )))

(defn- get-number-of-games-per-player-sql [conferenceid]
  (s/build :select :t.gamesperplayer 
           :from [[:conference :c]]
           :join [[:tournament :t] [:= :c.tournamentid :t.id]] 
           :where [:= :c.id conferenceid]
           :order-by [:t.id]))

(defn get-number-of-games-per-player [conferenceid]
  (j/query db/db
    (s/format (get-number-of-games-per-player-sql conferenceid))
    ))

(defn- get-conferences-sql [tournamentid]
  (s/build :select [:name :id] 
           :from [[:conference :c]]
           :where [:= :c.tournamentid tournamentid]
           :order-by [:c.id]))

(defn get-conferences [tournamentid]
  (j/query db/db 
    (s/format (get-conferences-sql tournamentid))
    ))


