(ns shl.dao.user
  (require [clojure.java.jdbc :as j]
           [honeysql.core :as s]
           [shl.dao.db :as db]))

(defn add-user [username firstname lastname roleid]
  (j/insert! db/db :user_ {:username username 
                          :firstname firstname
                          :lastname lastname
                          :roleid roleid}))

(defn- get-roles-sql []
  (s/build :select   :* 
           :from     [:role]
           :order-by [:id]))

(defn get-roles []
  (j/query db/db 
    (s/format (get-roles-sql))))

(defn- get-role-id-sql [rolename]
  (s/build :select :r.id 
           :from   [[:role :r]] 
           :where  [:= :r.name rolename]
           :limit  1))

(defn get-role-id []
  (j/query db/db 
    (s/format (get-role-id-sql))))

(defn- get-users-sql []
  (s/build :select   :* 
           :from     [:user_]
           :order-by [:id]))

(defn get-users []
  (j/query db/db 
    (s/format (get-users-sql))))

(defn- get-user-sql [username]
  (s/build :select [:u.* 
                    [:r.name "role"] 
                    [:p.id   "playerid"]] 
           :from   [[:user_ :u]]
           :join   [[:role :r] [:= :r.id :u.roleid]
                    [:player :p] [:= :p.userid :u.id]
                    [:conference :c] [:= :c.id :p.conferenceid]
                    [:tournament :t] [:and [:= :t.id :c.tournamentid] 
                                           [:= :t.active true]]]
           :where  [:= :u.username username]
           :limit  1))

(defn get-user [username]
  (first(j/query db/db 
    (s/format (get-user-sql username)))))
