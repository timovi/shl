(ns shl.service.game
  (:require [shl.dao.game :as game-dao]
            [shl.dao.player :as player-dao]
            [shl.dao.conference :as conference-dao]))

(defn- add-game [playerid opponentid home]
  (if (home) 
    (game-dao/add-game playerid opponentid)
    (game-dao/add-game opponentid playerid)))

(defn- add-games [playerid opponentid home count]
  (when (> 0 count)
    (add-game playerid opponentid home)
    (add-games playerid opponentid (not home) (- count))))

(defn- add-games [playerid players count]
  (let [opponentid (first players)]
    (if-not (= playerid opponentid) 
      (add-games playerid opponentid true count))
    (add-games playerid (rest players) count)))

(defn add-games [userid conferenceid]
  (let [playerid (player-dao/get-player-id userid conferenceid)
        players (player-dao/get-playerids conferenceid)
        game-count (conference-dao/get-number-of-games-per-player conferenceid)]
       (add-games playerid players game-count)))