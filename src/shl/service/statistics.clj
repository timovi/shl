(ns shl.service.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.dao.game :as game-dao]
           [shl.dao.player :as player-dao]))

(def points {:win 3 
             :ot-win 2 
             :ot-defeat 1
             :defeat 0
             :not-played 0})

(defn- map-game [result goals-scored goals-let]
  {:result result 
   :goals-scored goals-scored
   :goals-let goals-let
   :points (get points result)})

(defn- get-game-result [game home]
  (let [player-goals (if home :home-goals :away-goals)
        opponent-goals (if home :away-goals :home-goals)
        player-won (> (get game player-goals) (get game opponent-goals))
        overtime (get game :overtime)
        playdate (get game :playdate)]
    (cond 
      (and (nil? playdate)) (map-game :not-played 0 0) 
      (and player-won (not overtime)) (map-game :win player-goals opponent-goals)
      (and player-won overtime) (map-game :ot-win player-goals opponent-goals)
      (and (not player-won) overtime) (map-game :ot-defeat player-goals opponent-goals)
      :else (map-game :defeat player-goals opponent-goals))))

(defn- get-game-results [games home]
  (map #(get-game-result % home) games))

(defn- get-player-game-results [playerid games]
  (let [home-games (group-by :home-player games)
        away-games (group-by :away-player games)]
    (concat
       (map (get-game-results (get home-games playerid) true))
       (map (get-game-results (get away-games playerid) false)))))

(defn get-player-standings [playerid]
  (let [games (game-dao/get-player-games playerid)
        results (get-player-game-results playerid games)])) 

(defn get-conference-standings [conferenceid]
  (let [players (player-dao/get-playerids)
        games (game-dao/get-conference-games conferenceid)
        home-games (group-by :home-player games)
        away-games (group-by :away-player games)]
    (merge-with concat
       (map #((get-game-results (get home-games %) true) home-games) players)
       (map #((get-game-results (get away-games %) false) away-games) players)
    )))

(defn get-tournament-standings [tournamentid]
  true)