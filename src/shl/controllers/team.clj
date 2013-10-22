(ns shl.controllers.team
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.player :as dao]))

(defn get-teams []
  (json/write-str (dao/get-teams)))

(defn add [name]
  (when-not (str/blank? name)
    (dao/add-team name)
  (true)))

(defroutes routes
  (GET  "/teams/get.api" [] (get-users))
  (POST "/team/add.api" [name] (add name)))

