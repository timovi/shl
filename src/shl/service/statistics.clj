(ns shl.service.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.dao.game :as game-dao]))

;;(defn get-player-statistics [playerid]
;;  (let [games (game/get-player-games playerid)])
;;  (let [home-games ()
;;  )

(def points {:win 3 
             :ot-win 2 
             :ot-defeat 1
             :defeat 0})

(defn get-game-status [game]
  (let [player-won (> (get game :player-goals) (get game :opponent-goals))
        overtime (get game :overtime)]
    (cond 
      (and player-won (not overtime)) :win
      (and player-won overtime) :ot-win
      (and (not player-won) overtime) :ot-defeat
      :else :defeat)))

(defn get-conference-standings [conferenceid]
  (let [games (game-dao/get-conference-games conferenceid)]
    
    ))

(defn get-tournament-standings [tournamentid]
  true)