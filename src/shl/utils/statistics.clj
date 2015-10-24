(ns shl.utils.statistics
  (require [clj-time.coerce :as time]
           [clj-time.core :as t]))

(def points {:win        3 
             :otwin      2
             :otdefeat   1
             :defeat     0
             :notplayed  0})

(defn- map-game 
  "Returns a map containing the game results given in the parameters plus the points from the game"
  [gameid playerid opponentid firstname lastname team result goals-scored goals-let]
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

(defn- get-game-result 
  "Calculates a results map for the player from a single game"
  [playerid game home]
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

(defn- get-game-results 
  "A helper function to calculate a list of game results using either the home or the away games of the player"
  [playerid games home]
  (map #(get-game-result playerid % home) games))

(defn get-player-game-results 
  "Calculates game results from all of the games the player has played in"
  [playerid games]
  (let [home-games (group-by :homeplayerid games)
        away-games (group-by :awayplayerid games)]
    (concat
       (get-game-results playerid (get home-games playerid) true)
       (get-game-results playerid (get away-games playerid) false))))

(defn filter-player-games 
  "Filters the games where the given player has played (home or away) from a sequence of games"
  [playerid all-games]
  (filter #(or (= (% :homeplayerid) playerid) (= (% :awayplayerid) playerid)) all-games))

(defn- inc-game-result 
  "Increases the previous result if the current game's result is the same as the key. Otherwise returns the previous value of the result-key"
  [prev current-game key result-key]
  (if (= (current-game :result) key) 
    (inc (prev result-key)) 
    (prev result-key)))

(defn- inc-number-of-games 
  "Increases the number of games played if the result of the current game is some other than :notplayed"
  [prev current-game]
  (if (not (= (current-game :result) :notplayed))
    (inc (prev :games)) 
    (prev :games)))

(defn- inc-plusminus 
  "Returns a new plus-minus result by adding goal counts of a single game to a previous result"
  [prev goals-scored-in-game goals-let-in-game]
  (- (+ prev goals-scored-in-game) goals-let-in-game))

(defn calculate-player-standings 
  "Calculates the standings results (number of games, wins, defeats, goal amounts, points, etc) of a single player from the player's game results"
  [player-game-stats]
  (reduce 
      (fn[prev current-game] 
        {:playerid     (prev :playerid)
         :firstname    (prev :firstname)
         :lastname     (prev :lastname)
         :team         (prev :team)
         :games        (inc-number-of-games prev current-game)
         :wins         (inc-game-result prev current-game :win :wins)
         :otwins       (inc-game-result prev current-game :otwin :otwins)
         :otdefeats    (inc-game-result prev current-game :otdefeat :otdefeats)
         :defeats      (inc-game-result prev current-game :defeat :defeats)
         :notplayed    (inc-game-result prev current-game :notplayed :notplayed)
         :goalsscored  (+ (prev :goalsscored) (current-game :goalsscored)) 
         :goalslet     (+ (prev :goalslet) (current-game :goalslet))
         :plusminus    (inc-plusminus (prev :plusminus) (current-game :goalsscored) (current-game :goalslet))
         :points       (+ (prev :points) (current-game :points))
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

(defn calculate-conference-standings 
  "Calculates the standings results (number of games, wins, defeats, goal amounts, points, etc) of a all the players in a conference"
  [conference-game-stats]
  (map #(calculate-player-standings %) conference-game-stats))
