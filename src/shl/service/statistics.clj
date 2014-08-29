(ns shl.service.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]
           [shl.dao.game :as game-dao]
           [shl.dao.player :as player-dao]
           [shl.dao.conference :as conference-dao]))

(def points {:win 3 
             :ot-win 2 
             :ot-defeat 1
             :defeat 0
             :not-played 0})

(defn map-game [playerid result goals-scored goals-let]
  {:playerid playerid
   :result result 
   :goals-scored goals-scored
   :goals-let goals-let
   :points (get points result)})

(defn- get-game-result [playerid game home]
  (let [player-goals-key (if home :homegoals :awaygoals)
        player-goals (or (get game player-goals-key) 0)
        opponent-goals-key (if home :awaygoals :homegoals)
        opponent-goals (or (get game opponent-goals-key) 0)
        player-won (> player-goals opponent-goals)
        overtime (get game :overtime)
        playdate (get game :playdate)]
    (cond 
      (nil? playdate) (map-game playerid :not-played 0 0) 
      (and player-won (not overtime)) (map-game playerid :win player-goals opponent-goals)
      (and player-won overtime) (map-game playerid :ot-win player-goals opponent-goals)
      (and (not player-won) overtime) (map-game playerid :ot-defeat player-goals opponent-goals)
      :else (map-game playerid :defeat player-goals opponent-goals))))

(defn- get-game-results [playerid games home]
  (map #(get-game-result playerid % home) games))

(defn- get-player-game-results [playerid games]
  (let [home-games (group-by :homeplayerid games)
        away-games (group-by :awayplayerid games)]
    (concat
       (get-game-results playerid (get home-games playerid) true)
       (get-game-results playerid (get away-games playerid) false))))

(defn get-player-stats [playerid]
  (let [games (game-dao/get-player-games playerid)]
    (get-player-game-results playerid games)))


(defn get-conference-stats [conferenceid]
  (let [players (player-dao/get-playerids conferenceid)
        games (game-dao/get-conference-games conferenceid)
        home-games (group-by :homeplayerid games)
        away-games (group-by :awayplayerid games)]
    (concat
      (map #(get-game-results (% :id) (get home-games (% :id)) true) players)
      (map #(get-game-results (% :id) (get away-games (% :id)) true) players)
    )))

(defn inc-result [prev curr result-key]
  (if (= (curr :result) result-key) 
    (inc (prev result-key)) 
    (prev result-key)))

(defn get-player-standings [playerid]
  (let [stats (get-player-stats playerid)]
    (reduce 
      (fn[prev curr] 
        {:playerid (prev :playerid)
         :win (inc-result prev curr :win)
         :ot-win (inc-result prev curr :ot-win)
         :ot-defeat (inc-result prev curr :ot-defeat)
         :defeat (inc-result prev curr :defeat)
         :not-played (inc-result prev curr :not-played)
         :goals-scored (+ (prev :goals-scored) (curr :goals-scored)) 
         :goals-let (+ (prev :goals-let) (curr :goals-let))
         :points (+ (prev :points) (curr :points))
        }) 
      {:playerid playerid :win 0 :ot-win 0 :ot-defeat 0 :defeat 0 :not-played 0 :goals-scored 0 :goals-let 0 :points 0}
      stats)))


(defn get-conference-standings [conferenceid]
  (let [players (player-dao/get-playerids conferenceid)
        games (game-dao/get-conference-games conferenceid)
        home-games (group-by :homeplayerid games)
        away-games (group-by :awayplayerid games)]
    (sort-by :points 
      (concat
        (map #(get-game-results (% :id) (get home-games (% :id)) true) players)
        (map #(get-game-results (% :id) (get away-games (% :id)) true) players)
    ))))

(defn get-tournament-standings [tournamentid]
  (let [conferences (conference-dao/get-conferences tournamentid)]
    (sort-by :points
      (concat
         get-conference-standings))))