(ns shl.service.game
  (:require [shl.service.chat :as chat-service]
            [shl.dao.game :as game-dao]
            [shl.dao.player :as player-dao]
            [shl.dao.conference :as conference-dao]))

(defn- add-game 
  "Adds a single game for the player"
  [playerid opponentid home]
  (if (= home true) 
    (game-dao/add-game playerid opponentid)
    (game-dao/add-game opponentid playerid)))

(defn- add-games-between-two-players 
  "Adds a number of games between two players alternating the home player and the away player in the games"
  [playerid opponentid count home]
  (when (> count 0)
    (add-game playerid opponentid home)
    (add-games-between-two-players playerid opponentid (- count 1) (not home))))

(defn- add-games-for-player 
  "Adds games for the player against all other players. The count affects how many games are created between each player"
  [playerid players count]
  (if-not (empty? players)
    (let [opponentid (first players)]
      (if-not (= playerid opponentid) 
        (add-games-between-two-players playerid opponentid count true))
      (add-games-for-player playerid (rest players) count))))

(defn add-games 
  "Gets user's player id in a tournament (defined by the conference id) and adds games for the player agains all other players in the conference"
  [userid conferenceid]
  (let [playerid (:id (player-dao/get-player-id userid conferenceid))
        players (map :id (player-dao/get-playerids conferenceid))
        game-count (:gamesperplayer (conference-dao/get-number-of-games-per-player conferenceid))]
       (add-games-for-player playerid players game-count)))

(defn update-game 
  "Updates the game result and send a hipchat notification"
  [gameid home-goals away-goals overtime shootout playdate]
  (let [game-info (game-dao/get-game-notification-info gameid)]
    (game-dao/update-game gameid home-goals away-goals overtime shootout playdate)
    (chat-service/send-notification (:hipchatchannelid game-info)
                                    "green"
                                    (str (:conferencename game-info) ": "
                                         (:hometeam game-info) 
                                         " <b>"
                                         home-goals " - " 
                                         away-goals
                                         (cond shootout " RL" overtime " JA" :else "")
                                         "</b> "
                                         (:awayteam game-info) ". "
                                         (cond (> home-goals away-goals) (str "Hyvä " (:homefirstname game-info) "!")
                                               (< home-goals away-goals) (str "Hyvä " (:awayfirstname game-info) "!")
                                               :else "Tasapeli!")))))