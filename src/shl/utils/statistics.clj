(ns shl.utils.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]))

(def points {:win        3 
             :ot-win     2
             :ot-defeat  1
             :defeat     0
             :not-played 0})

(defn- map-game [gameid playerid opponentid firstname lastname team result goals-scored goals-let]
  {:gameid       gameid
   :playerid     playerid
   :opponentid   opponentid
   :firstname    firstname
   :lastname     lastname
   :team         team
   :result       result 
   :goals-scored goals-scored
   :goals-let    goals-let
   :points       (get points result)})

(defn- get-game-result [playerid game home]
  (let [gameid             (get game :id)
        opponentid         (if home (get game :awayplayerid)  (get game :homeplayerid))
        firstname          (if home (get game :homefirstname) (get game :awayfirstname))
        lastname           (if home (get game :homelastname)  (get game :awaylastname))
        team               (if home (get game :hometeam)      (get game :awayteam))
        player-goals-key   (if home :homegoals :awaygoals)
        player-goals       (or (get game player-goals-key) 0)
        opponent-goals-key (if home :awaygoals :homegoals)
        opponent-goals     (or (get game opponent-goals-key) 0)
        player-won         (> player-goals opponent-goals)
        player-lost        (< player-goals opponent-goals)
        overtime           (get game :overtime)
        playdate           (get game :playdate)]
    (cond 
      (and player-won (not overtime))  (map-game gameid playerid opponentid firstname lastname team :win player-goals opponent-goals)
      (and player-won overtime)        (map-game gameid playerid opponentid firstname lastname team :ot-win player-goals opponent-goals)
      (and player-lost overtime)       (map-game gameid playerid opponentid firstname lastname team :ot-defeat player-goals opponent-goals)
      (and player-lost (not overtime)) (map-game gameid playerid opponentid firstname lastname team :defeat player-goals opponent-goals)
      :else                            (map-game gameid playerid opponentid firstname lastname team :not-played player-goals opponent-goals))))

(defn- get-game-results [playerid games home]
  (map #(get-game-result playerid % home) games))

(defn get-player-game-results [playerid games]
  (let [home-games (group-by :homeplayerid games)
        away-games (group-by :awayplayerid games)]
    (concat
       (get-game-results playerid (get home-games playerid) true)
       (get-game-results playerid (get away-games playerid) false))))

(defn filter-player-games [playerid all-games]
  (filter #(or (= (% :homeplayerid) playerid) (= (% :awayplayerid) playerid)) all-games))

(defn- inc-result [prev curr result-key]
  (if (= (curr :result) result-key) 
    (inc (prev result-key)) 
    (prev result-key)))

(defn calculate-player-standings [player-game-stats]
  (reduce 
      (fn[prev curr] 
        {:playerid     (prev :playerid)
         :firstname    (prev :firstname)
         :lastname     (prev :lastname)
         :team         (prev :team)
         :win          (inc-result prev curr :win)
         :ot-win       (inc-result prev curr :ot-win)
         :ot-defeat    (inc-result prev curr :ot-defeat)
         :defeat       (inc-result prev curr :defeat)
         :not-played   (inc-result prev curr :not-played)
         :goals-scored (+ (prev :goals-scored) (curr :goals-scored)) 
         :goals-let    (+ (prev :goals-let) (curr :goals-let))
         :points       (+ (prev :points) (curr :points))
        }) 
      {:playerid     (:playerid  (first player-game-stats)) 
       :firstname    (:firstname (first player-game-stats))
       :lastname     (:lastname (first player-game-stats)) 
       :team         (:team      (first player-game-stats)) 
       :win          0 
       :ot-win       0 
       :ot-defeat    0 
       :defeat       0 
       :not-played   0 
       :goals-scored 0 
       :goals-let    0 
       :points       0}
      player-game-stats))

(defn calculate-conference-standings [conference-game-stats]
    (map #(calculate-player-standings %) conference-game-stats))
