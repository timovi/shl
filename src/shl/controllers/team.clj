(ns shl.controllers.team
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.player :as dao]))

(defn get-teams []
  (response (dao/get-teams)))

(defn add [name]
  (when-not (str/blank? name)
    (dao/add-team name)
    (response (dao/get-team name))))

(defroutes app-routes
  (context "/teams" [] (defroutes team-routes
    (GET  "/" [] (get-teams))
    (POST "/" [name] (add name)))))