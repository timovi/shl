(ns shl.dao.game
  (require [clojure.java.jdbc :as j]
           [clojure.java.jdbc.sql :as sql]
           [honeysql.core :as s]
           [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.utils.time :as time-utils]
           [shl.dao.db :as db]))

(defn add-game [home-playerid away-playerid]
  (j/insert! db/db :game {:homeplayerid home-playerid
                          :awayplayerid away-playerid
                          :homegoals 0
                          :awaygoals 0
                          :overtime false
                          :shootout false}))

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
  (s/build :select    [:g.id 
                       :g.homegoals
                       :g.awaygoals 
                       :g.overtime 
                       :g.shootout
                       :g.playdate 
                       :g.modifieddate
                       [:hu.id         "homeuserid"] 
                       [:hu.firstname  "homefirstname"] 
                       [:hu.lastname   "homelastname"]
                       [:hp.id         "homeplayerid"]
                       [:ht.name       "hometeam"]
                       [:au.id         "awayuserid"] 
                       [:au.firstname  "awayfirstname"] 
                       [:au.lastname   "awaylastname"]
                       [:ap.id         "awayplayerid"]
                       [:at.name       "awayteam"]] 
           :from       [[:game :g]]
           :join       [[:player :hp] [:= :g.homeplayerid :hp.id]]
           :merge-join [[:user_ :hu]  [:= :hp.userid :hu.id]]
           :merge-join [[:team :ht]   [:= :hp.teamid :ht.id]]
           :merge-join [[:player :ap] [:= :g.awayplayerid :ap.id]]
           :merge-join [[:user_ :au]  [:= :ap.userid :au.id]]
           :merge-join [[:team :at]   [:= :ap.teamid :at.id]]
           :where      [:= :hp.conferenceid conferenceid]
           :order-by   [:hu.lastname :hu.firstname :au.lastname :au.firstname]))

(defn get-conference-games [conferenceid]
  (j/query db/db 
    (s/format (get-conference-games-sql conferenceid))
    :row-fn #(assoc % :playdate (str (time-utils/from-sql-date (% :playdate)))
                      :modifieddate (str (time-utils/from-sql-date (% :modifieddate)))) 
    ))

(defn- get-game-notification-info-sql [gameid]
  (s/build :select    [[:c.hipchatchannelid "hipchatchannelid"]
                       [:c.name             "conferencename"] 
                       [:hu.firstname       "homefirstname"] 
                       [:hu.lastname        "homelastname"]
                       [:ht.name            "hometeam"]
                       [:au.firstname       "awayfirstname"] 
                       [:au.lastname        "awaylastname"]
                       [:ap.id              "awayplayerid"]
                       [:at.name            "awayteam"]]
           :from       [[:game :g]]
           :join       [[:player :hp]    [:= :g.homeplayerid :hp.id]]
           :merge-join [[:user_ :hu]     [:= :hp.userid :hu.id]]
           :merge-join [[:team :ht]      [:= :hp.teamid :ht.id]]
           :merge-join [[:player :ap]    [:= :g.awayplayerid :ap.id]]
           :merge-join [[:user_ :au]     [:= :ap.userid :au.id]]
           :merge-join [[:team :at]      [:= :ap.teamid :at.id]]
           :merge-join [[:conference :c] [:= :hp.conferenceid :c.id]]
           :where      [:= :g.id gameid]
           :limit      1))

(defn get-game-notification-info [gameid]
  (first (j/query db/db
    (s/format (get-game-notification-info-sql gameid)))))


(defn- get-game-sql [gameid]
    (s/build :select :* 
             :from [[:game :g]] 
             :where [:= :g.id gameid]
             :limit 1))

(defn get-game [gameid]
    (first (j/query db/db 
       (s/format (get-game-sql gameid))
       :row-fn #(assoc % :playdate (str (time-utils/from-sql-date (% :playdate)))
                         :modifieddate (str (time-utils/from-sql-date (% :modifieddate))))
    )))

(defn- get-player-games-sql [playerid]
  (s/build :select [:g.id 
                    :g.homegoals 
                    :g.awaygoals 
                    :g.overtime 
                    :g.shootout
                    :g.playdate 
                    :g.modifieddate
                    [:hu.id         "homeuserid"]
                    [:hp.id         "homeplayerid"]
                    [:hu.firstname  "homefirstname"] 
                    [:hu.lastname   "homelastname"]
                    [:ht.name       "hometeam"]
                    [:au.id         "awayuserid"] 
                    [:au.firstname  "awayfirstname"] 
                    [:au.lastname   "awaylastname"]
                    [:ap.id         "awayplayerid"]
                    [:at.name       "awayteam"]] 
           :from [[:game :g]]
           :join [[:player :hp][:= :g.homeplayerid :hp.id]]
           :merge-join [[:user_ :hu][:= :hp.userid :hu.id]]
           :merge-join [[:team :ht][:= :hp.teamid :ht.id]]
           :merge-join [[:player :ap][:= :g.awayplayerid :ap.id]]
           :merge-join [[:user_ :au][:= :ap.userid :au.id]]
           :merge-join [[:team :at][:= :ap.teamid :at.id]]
           :where [:or [:= :hp.id playerid]
                       [:= :ap.id playerid]]
           :order-by [:hu.lastname :hu.firstname :au.lastname :au.firstname]))

(defn get-player-games [playerid]
  (j/query db/db 
    (s/format (get-player-games-sql playerid))
    :row-fn #(assoc % :playdate (str (time-utils/from-sql-date (% :playdate)))
                      :modifieddate (str (time-utils/from-sql-date (% :modifieddate)))) 
   ))

(defn- get-player-game-results-sql [playerid]
  (s/build :select     [:g.homegoals 
                        :g.awaygoals 
                        :g.overtime
                        [:hp.id      "homeplayerid"]
                        [:ap.id      "awayplayerid"]]  
           :from       [[:game :g]]
           :join       [[:player :hp][:= :g.homeplayerid :hp.id]]
           :merge-join [[:player :ap][:= :g.awayplayerid :ap.id]]
           :where      [:and [:<= :g.playdate (time/to-sql-date (t/now))]
                             [:or [:= :hp.id playerid]
                                  [:= :ap.id playerid]]]
           :order-by   [:g.id]))

(defn get-player-game-results [playerid]
  (j/query db/db 
    (s/format (get-player-game-results-sql playerid))))