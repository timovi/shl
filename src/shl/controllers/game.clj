(ns shl.controllers.game
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [clj-time.format :as time]
            [shl.utils.time :as time-utils]
            [shl.dao.game :as dao]))

(defn update [gameid home-goals away-goals overtime shootout playdate]
  (when-not (nil? playdate)
    (dao/update-game gameid home-goals away-goals overtime shootout
                    (time/parse time-utils/formatter playdate)))
  (response (dao/get-game gameid)))

(defn get-conference-games [conferenceid]
  (response (dao/get-conference-games (Integer/parseInt conferenceid))))

(defroutes app-routes
  (context "/games" [] (defroutes game-routes
    (GET ["/conference/:id/" :id #"[0-9]+"] [id] (get-conference-games id))
    (POST "/" [id homegoals awaygoals 
              overtime shootout playdate] 
      (update id homegoals awaygoals overtime shootout playdate)))))

