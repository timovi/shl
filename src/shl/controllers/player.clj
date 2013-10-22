(ns shl.controllers.player
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.service.game :as game-service]
            [shl.dao.game :as game-dao]
            [shl.dao.player :as player-dao]
            [shl.dao.conference :as conference-dao]))

(defn get-players [conferenceid]
  (json/write-str (player-dao/get-players (Integer/parseInt conferenceid))))

(defn add [userid conferenceid teamid]
  (when-not (and (str/blank? userid)
                 (str/blank? conferenceid)
                 (str/blank? teamid))
    (player-dao/add-player (Integer/parseInt userid)
                           (Integer/parseInt conferenceid)
                           (Integer/parseInt teamid))
    (game-service/add-games userid conferenceid)
  (true)))

(defn remove [playerid]
  (when-not (str/blank? playerid)
    (let [playerid-int (Integer/parseInt playerid)]
      (player-dao/remove-player (Integer/parseInt playerid-int))
      (game-dao/remove-games playerid-int))
  (true)))

(defroutes routes
  (GET  "/players/get.api" [conferenceid] (get-players conferenceid))
  (POST "/player/add.api" [userid conferenceid teamid] 
        (add userid conferenceid teamid))
  (POST "/player/remove.api" [playerid] 
        (remove playerid)))

