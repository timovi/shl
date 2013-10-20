(ns shl.service.game
  (require [clojure.java.jdbc :as j]
           [clojure.java.jdbc.sql :as sql]
           [honeysql.core :as s]
           [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.service.db :as db]))

(defn add-game [home-playerid away-playerid]
  (j/insert! db/db :game {:homeplayerid home-playerid
                          :awayplayerid away-playerid}))

;; Using the traditional sql-string because 
;; a) honeySql doesn't yet support DELETE clause
;; b) clojure jdbc doesn't support OR clauses in it's own API
(defn remove-games [playerid]
  (j/execute! db/db 
    ["DELETE FROM game g WHERE g.homeplayerid = ? OR g.awayplayerid = ?" playerid playerid]))

(defn update-game [gameid home-goals away-goals overtime shootout playdate]
  (j/update! db/db 
             :game {:homegoals home-goals
                    :awaygoals away-goals
                    :overtime overtime
                    :shootout shootout
                    :playdate (time/to-sql-date playdate)
                    :modifieddate (time/to-sql-date (t/now))}
             (sql/where {:id gameid})))

(defn- get-conference-games-sql [conferenceid]
  (s/build :select [:g.id :g.homegoals :g.awaygoals 
                    :g.overtime :g.shootout
                    :g.playdate :g.modifieddate
                    [:hu.id "homeuserid"] 
                    [:hu.firstname "homefirstname"] 
                    [:hu.lastname "homelastname"]
                    [:hp.id "homeplayerid"]
                    [:ht.name "hometeam"]
                    [:au.id "awayuserid"] 
                    [:au.firstname "awayfirstname"] 
                    [:au.lastname "awaylastname"]
                    [:ap.id "awayplayerid"]
                    [:at.name "awayteam"]] 
           :from [[:game :g]]
           :join [[:player :hp][:= :g.homeplayerid :hp.id]]
           :merge-join [[:user_ :hu][:= :hp.userid :hu.id]]
           :merge-join [[:team :ht][:= :hp.teamid :ht.id]]
           :merge-join [[:player :ap][:= :g.awayplayerid :ap.id]]
           :merge-join [[:user_ :au][:= :ap.userid :au.id]]
           :merge-join [[:team :at][:= :ap.teamid :at.id]]
           :where [:= :hp.conferenceid conferenceid]
           :order-by [:hu.lastname :hu.firstname :au.lastname :au.firstname]))

(defn get-conference-games [conferenceid]
  (j/query db/db 
    (s/format (get-conference-games-sql conferenceid))))

(defn- get-user-games-sql [userid]
  (s/build :select [:g.id :g.homegoals :g.awaygoals 
                    :g.overtime :g.shootout
                    :g.playdate :g.modifieddate
                    [:hu.id "homeuserid"]
                    [:hp.id "homeplayerid"]
                    [:hu.firstname "homefirstname"] 
                    [:hu.lastname "homelastname"]
                    [:ht.name "hometeam"]
                    [:au.id "awayuserid"] 
                    [:au.firstname "awayfirstname"] 
                    [:au.lastname "awaylastname"]
                    [:ap.id "awayplayerid"]
                    [:at.name "awayteam"]] 
           :from [[:game :g]]
           :join [[:player :hp][:= :g.homeplayerid :hp.id]]
           :merge-join [[:user_ :hu][:= :hp.userid :hu.id]]
           :merge-join [[:team :ht][:= :hp.teamid :ht.id]]
           :merge-join [[:player :ap][:= :g.awayplayerid :ap.id]]
           :merge-join [[:user_ :au][:= :ap.userid :au.id]]
           :merge-join [[:team :at][:= :ap.teamid :at.id]]
           :where [:or [:= :hu.id userid]
                       [:= :au.id userid]]
           :order-by [:hu.lastname :hu.firstname :au.lastname :au.firstname]))

(defn get-user-games [userid]
  (j/query db/db 
    (s/format (get-user-games-sql userid))))