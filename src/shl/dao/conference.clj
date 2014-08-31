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
                                :active false}))

(defn add-conference [name tournamentid]
  (j/insert! db/db :conference {:name name 
                                :tournamentid tournamentid}))

(defn inactivate-tournaments []
  (j/update! db/db :tournament {:active false} ["active = ?" true]))

(defn activate-tournament [tournamentid]
  (j/update! db/db :tournament {:active true} ["id = ?" tournamentid]))

(defn- get-active-tournament-sql []
    (s/build :select :* 
             :from [[:tournament :t]] 
             :where [:= :t.active true]
             :order-by [[:t.id :desc]]
             :limit 1))

(defn get-active-tournament []
  (first 
    (j/query db/db 
       (s/format (get-active-tournament-sql))
       :row-fn #(assoc % :startdate (str (time-utils/from-sql-date (% :startdate)))
                         :enddate (str (time-utils/from-sql-date (% :enddate))))
    )))

(defn- get-tournaments-sql []
    (s/build :select :* 
             :from [[:tournament :t]] 
             :order-by [[:t.id :desc]]))

(defn get-tournaments []
    (j/query db/db 
       (s/format (get-tournaments-sql))
       :row-fn #(assoc % :startdate (str (time-utils/from-sql-date (% :startdate)))
                         :enddate (str (time-utils/from-sql-date (% :enddate))))
    ))

(defn- get-tournament-sql [tournamentid]
    (s/build :select :* 
             :from [[:tournament :t]] 
             :where [:= :t.id tournamentid]
             :order-by [:t.id]
             :limit 1))

(defn get-tournament [tournamentid]
    (first (j/query db/db 
       (s/format (get-tournament-sql tournamentid))
       :row-fn #(assoc % :startdate (str (time-utils/from-sql-date (% :startdate)))
                         :enddate (str (time-utils/from-sql-date (% :enddate))))
    )))

(defn- get-tournament-by-name-sql [name]
    (s/build :select :* 
             :from [[:tournament :t]] 
             :where [:= :t.name name]
             :order-by [:t.id]
             :limit 1))

(defn get-tournament-by-name [name]
    (first (j/query db/db 
       (s/format (get-tournament-by-name-sql name))
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
  (first (j/query db/db
    (s/format (get-number-of-games-per-player-sql conferenceid))
    )))

(defn- get-conferences-sql [tournamentid]
  (s/build :select [:name :id] 
           :from [[:conference :c]]
           :where [:= :c.tournamentid tournamentid]
           :order-by [:c.id]))

(defn get-conferences [tournamentid]
  (j/query db/db 
    (s/format (get-conferences-sql tournamentid))
    ))

(defn- get-conference-sql [name tournamentid]
  (s/build :select :* 
           :from [[:conference :c]]
           :where [:and [:= :c.name name]
                        [:= :c.tournamentid tournamentid]]
           :order-by [:c.id]
           :limit 1))

(defn get-conference [name tournamentid]
  (first (j/query db/db 
    (s/format (get-conference-sql name tournamentid))
    )))

(defn- get-conferenceids-sql [tournamentid]
  (s/build :select [:c.id] 
           :from [[:conference :c]]
           :where [:= :c.tournamentid tournamentid] 
           :order-by [:c.id]))

(defn get-conferenceids [tournamentid]
  (j/query db/db 
    (s/format (get-conferenceids-sql tournamentid))
    ))
