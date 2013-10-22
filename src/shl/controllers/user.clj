(ns shl.controllers.user
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.user :as dao]))

(defn get-users []
  (json/write-str (dao/get-users)))

(defn get-user [username]
  (when-not (str/blank? username) 
    (json/write-str (dao/get-user username))))

(defn add [username firstname lastname roleid]
  (when-not (and (str/blank? username) 
                 (str/blank? firstname) 
                 (str/blank? lastname)
                 (str/blank? roleid))
    (dao/add-user username firstname lastname roleid))
  (true))

(defroutes routes
  (GET  "/users/get.api" [] (get-users))
  (GET  "/user/get.api" [username] (get-user username))
  (POST "/user/add.api" [username firstname lastname roleid] 
        (add username firstname lastname roleid)))

