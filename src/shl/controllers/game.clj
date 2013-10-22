(ns shl.controllers.game
  (:use [compojure.core :only (defroutes GET POST)])
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
  (true))

(defroutes routes
  (POST "/game/update.api" [gameid home-goals away-goals 
                            overtime shootout playdate] 
        (update gameid home-goals away-goals overtime shootout playdate)))

