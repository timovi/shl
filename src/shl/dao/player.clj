(ns shl.dao.player
  (require [clojure.java.jdbc :as j]
           [clojure.java.jdbc.sql :as sql]
           [honeysql.core :as s]
           [shl.dao.db :as db]))

(defn add-team [name]
  (j/insert! db/db :team {:name name}))

(defn add-player [userid conferenceid teamid]
  (j/insert! db/db :player {:userid userid
                            :conferenceid conferenceid
                            :teamid teamid}))

(defn remove-player [playerid]
  (j/delete! :player (sql/where {:id playerid})))

(defn- get-teams-sql []
  (s/build :select   [:*] 
           :from     [:team]
           :order-by [:id]))

(defn get-teams []
  (j/query db/db 
    (s/format (get-teams-sql))))

(defn- get-team-sql [name]
  (s/build :select   [:*] 
           :from     [:team]
           :where    [:= :name name]
           :order-by [:id]
           :limit    1))

(defn get-team [name]
  (first (j/query db/db 
    (s/format (get-team-sql name)))))

(defn- get-player-id-sql [userid conferenceid]
  (s/build :select   [:p.id]
           :from     [[:player :p]]
           :where    [:and [:= :p.userid userid] 
                           [:= :p.conferenceid conferenceid]]
           :order-by [:p.id]
           :limit    1))

(defn get-player-id [userid conferenceid]
  (first (j/query db/db
    (s/format (get-player-id-sql userid conferenceid)))))

(defn- get-player-sql [userid conferenceid]
  (s/build :select     [[:p.id        "playerid"]
                        [:u.firstname "firstname"]
                        [:u.lastname  "lastname"]
                        [:t.id        "teamid"]
                        [:t.name      "teamname"]
                        [:c.id        "conferenceid"]
                        [:c.name      "conferencename"]]
           :from       [[:player :p]]
           :join       [[:user_ :u]      [:= :u.id :p.userid]]
           :merge-join [[:team :t]       [:= :t.id :p.teamid]]
           :merge-join [[:conference :c] [:= :c.id :p.conferenceid]]
           :where      [:and [:= :p.userid userid] 
                             [:= :p.conferenceid conferenceid]]
           :order-by   [:p.id]
           :limit      1))

(defn get-player [userid conferenceid]
  (first (j/query db/db
    (s/format (get-player-sql userid conferenceid)))))

(defn- get-players-sql [conferenceid]
  (s/build :select     [[:p.id        "id"] 
                        [:u.firstname "firstname"] 
                        [:u.lastname  "lastname"] 
                        [:t.name      "team"]]
           :from       [[:player :p]]
           :join       [[:user_ :u] [:= :u.id :p.userid]]
           :merge-join [[:team :t] [:= :t.id :p.teamid]]
           :where      [:= :p.conferenceid conferenceid]
           :order-by   [:u.lastname :u.firstname]))

(defn get-players [conferenceid]
  (j/query db/db
    (s/format (get-players-sql conferenceid))))

(defn- get-playerids-sql [conferenceid]
  (s/build :select   [:p.id]
           :from     [[:player :p]]
           :where    [:= :p.conferenceid conferenceid]
           :order-by [:p.id]))

(defn get-playerids [conferenceid]
  (j/query db/db
    (s/format (get-playerids-sql conferenceid))))