(ns shl.controllers.player
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.service.game :as game-service]
            [shl.dao.game :as game-dao]
            [shl.dao.player :as player-dao]
            [shl.dao.conference :as conference-dao]))

(defn get-players [conferenceid]
  (response (player-dao/get-players (Integer/parseInt conferenceid))))

(defn add [userid conferenceid teamid]
  (when-not (and (str/blank? userid)
                 (str/blank? conferenceid)
                 (str/blank? teamid))
    (let [userid-int (Integer/parseInt userid)
          conferenceid-int (Integer/parseInt conferenceid)
          teamid-int (Integer/parseInt teamid)]
      (player-dao/add-player userid-int
                             conferenceid-int
                             teamid-int)
      (game-service/add-games userid-int conferenceid-int)
      (response (player-dao/get-player userid-int conferenceid-int)))))

(defn delete [playerid]
  (when-not (str/blank? playerid)
    (let [playerid-int (Integer/parseInt playerid)]
      (player-dao/remove-player (Integer/parseInt playerid-int))
      (game-dao/remove-games playerid-int)
      (response {:removed true}))))

(defroutes app-routes
  (context "/players" [] (defroutes player-routes
    (GET  ["/conference/:id/" :id #"[0-9]+"] [id] (get-players id))
    (POST "/" [userid conferenceid teamid] (add userid conferenceid teamid))
    (DELETE ["/:id/" :id #"[0-9]+"] [playerid] (delete playerid)))))
