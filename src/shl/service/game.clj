(ns shl.service.game
  (:require [shl.dao.game :as game-dao]
            [shl.dao.player :as player-dao]
            [shl.dao.conference :as conference-dao]))

(defn- add-game [playerid opponentid home]
  (if (= home true) 
    (game-dao/add-game playerid opponentid)
    (game-dao/add-game opponentid playerid)))

(defn- add-games-between-players [playerid opponentid count home]
  (when (> count 0)
    (add-game playerid opponentid home)
    (add-games-between-players playerid opponentid (- count 1) (not home))))

(defn- add-games-for-player [playerid players count]
  (if-not (empty? players)
    (let [opponentid (first players)]
      (if-not (= playerid opponentid) 
        (add-games-between-players playerid opponentid count true))
      (add-games-for-player playerid (rest players) count))))

(defn add-games [userid conferenceid]
  (let [playerid (:id (player-dao/get-player-id userid conferenceid))
        players (map :id (player-dao/get-playerids conferenceid))
        game-count (:gamesperplayer (conference-dao/get-number-of-games-per-player conferenceid))]
       (add-games-for-player playerid players game-count)))