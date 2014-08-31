(ns shl.controllers.user
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.user :as dao]))

(defn get-users []
  (response (dao/get-users)))

(defn get-user [username]
  (when-not (str/blank? username) 
    (response (dao/get-user username))))

(defn add [username firstname lastname roleid]
  (when-not (and (str/blank? username) 
                 (str/blank? firstname) 
                 (str/blank? lastname)
                 (str/blank? roleid))
    (dao/add-user username firstname lastname roleid)
    (response (dao/get-user username))))

(defroutes app-routes
  (context "/users" [] (defroutes user-routes
    (GET "/" [] (get-users))
    (GET "/:username/" [username] (get-user username)) 
    (POST "/" [username firstname lastname roleid] 
        (add username firstname lastname roleid)))))

