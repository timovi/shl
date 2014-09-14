(ns shl.utils.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]))

(def points {:win        3 
             :otwin      2
             :otdefeat   1
             :defeat     0
             :notplayed  0})

(defn- map-game [gameid playerid opponentid firstname lastname team result goals-scored goals-let]
  {:gameid       gameid
   :playerid     playerid
   :opponentid   opponentid
   :firstname    firstname
   :lastname     lastname
   :team         team
   :result       result 
   :goalsscored  goals-scored
   :goalslet     goals-let
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
      (and player-won overtime)        (map-game gameid playerid opponentid firstname lastname team :otwin player-goals opponent-goals)
      (and player-lost overtime)       (map-game gameid playerid opponentid firstname lastname team :otdefeat player-goals opponent-goals)
      (and player-lost (not overtime)) (map-game gameid playerid opponentid firstname lastname team :defeat player-goals opponent-goals)
      :else                            (map-game gameid playerid opponentid firstname lastname team :notplayed player-goals opponent-goals))))

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

(defn- inc-result [prev curr key result-key]
  (if (= (curr :result) key) 
    (inc (prev result-key)) 
    (prev result-key)))

(defn- calculate-number-of-games [prev curr]
  (if (not (= (curr :result) :notplayed))
    (inc (prev :games)) 
    (prev :games)))

(defn- calculate-plusminus [prev goals-scored goals-let]
  (- (+ prev goals-scored) goals-let))

(defn calculate-player-standings [player-game-stats]
  (reduce 
      (fn[prev curr] 
        {:playerid     (prev :playerid)
         :firstname    (prev :firstname)
         :lastname     (prev :lastname)
         :team         (prev :team)
         :games        (calculate-number-of-games prev curr)
         :wins         (inc-result prev curr :win :wins)
         :otwins       (inc-result prev curr :otwin :otwins)
         :otdefeats    (inc-result prev curr :otdefeat :otdefeats)
         :defeats      (inc-result prev curr :defeat :defeats)
         :notplayed    (inc-result prev curr :notplayed :notplayed)
         :goalsscored  (+ (prev :goalsscored) (curr :goalsscored)) 
         :goalslet     (+ (prev :goalslet) (curr :goalslet))
         :plusminus    (calculate-plusminus (prev :plusminus) (curr :goalsscored) (curr :goalslet))
         :points       (+ (prev :points) (curr :points))
        }) 
      {:playerid     (:playerid  (first player-game-stats)) 
       :firstname    (:firstname (first player-game-stats))
       :lastname     (:lastname  (first player-game-stats)) 
       :team         (:team      (first player-game-stats)) 
       :games        0
       :wins         0 
       :otwins       0 
       :otdefeats    0 
       :defeats      0 
       :notplayed    0 
       :goalsscored  0 
       :goalslet     0 
       :plusminus    0
       :points       0}
      player-game-stats))

(defn calculate-conference-standings [conference-game-stats]
    (map #(calculate-player-standings %) conference-game-stats))
