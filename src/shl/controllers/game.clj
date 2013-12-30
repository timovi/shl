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
  (when-not (and (str/blank? gameid)
                 (str/blank? home-goals)
                 (str/blank? away-goals)
                 (str/blank? overtime)
                 (str/blank? shootout)
                 (str/blank? playdate))
    (dao/update-game (Integer/parseInt gameid)
                     (Integer/parseInt home-goals)
                     (Integer/parseInt away-goals)
                     (Boolean/valueOf overtime)
                     (Boolean/valueOf shootout)
                     (time/parse time-utils/formatter playdate)))
  (response(true)))

(defn get-conference-games [conferenceid]
  (response (dao/get-conference-games (Integer/parseInt conferenceid))))

(defroutes app-routes
  (context "/games" [] (defroutes game-routes
    (GET ["/conference/:id" :id #"[0-9]+"] [id] (get-conference-games id))
    (PUT "/" [gameid home-goals away-goals 
              overtime shootout playdate] 
      (update gameid home-goals away-goals overtime shootout playdate)))))

