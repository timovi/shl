(ns shl.service.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.dao.game :as game-dao]
           [shl.dao.player :as player-dao]
           [shl.dao.conference :as conference-dao]
           [shl.utils.statistics :as stats-utils]))

(defn get-player-stats [playerid]
  (let [games (game-dao/get-player-games playerid)]
    (stats-utils/get-player-game-results playerid games)))


(defn get-conference-stats [conferenceid]
  (let [players    (player-dao/get-playerids conferenceid)
        games      (game-dao/get-conference-games conferenceid)]
    (map #(stats-utils/get-player-game-results (% :id) (stats-utils/filter-player-games (% :id) games)) players)))


(defn get-player-standings [playerid]
  (stats-utils/calculate-player-standings (get-player-stats playerid)))


(defn get-conference-standings [conferenceid]
  (let [stats (get-conference-stats conferenceid)]
    (reverse 
      (sort-by #(vec (map % [:points :wins :otwins :otdefeats :plusminus :goalsscored])) 
               (stats-utils/calculate-conference-standings stats)))))
