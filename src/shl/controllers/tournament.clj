(ns shl.controllers.tournament
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [clj-time.format :as time]
            [shl.utils.time :as time-utils]
            [shl.dao.conference :as dao]))

(defn get-active []
  (response (dao/get-active-tournament)))

(defn add [name startdate enddate games-per-player 
           playoff-teams-per-conference]
  (when-not (and (str/blank? name) 
                 (str/blank? enddate) 
                 (str/blank? games-per-player)
                 (str/blank? playoff-teams-per-conference))
    (dao/inactivate-tournaments)
    (dao/add-tournament name 
                        (time/parse time-utils/formatter startdate) 
                        (time/parse time-utils/formatter enddate) 
                        (Integer/parseInt games-per-player) 
                        (Integer/parseInt playoff-teams-per-conference)))
  (response true))

(defn get-conferences [tournamentid]
  (response (dao/get-conferences (Integer/parseInt tournamentid))))

(defn add-conference [name tournamentid]
  (when-not (and (str/blank? name) 
                 (str/blank? tournamentid))
    (dao/add-conference name (Integer/parseInt tournamentid)
  (response true))))

(defroutes app-routes
  (context "/tournaments" [] (defroutes tournament-routes
    (GET "/active" [] (get-active))
    (POST "/" [name startdate enddate games-per-player 
               playoff-teams-per-conference] 
        (add name startdate enddate games-per-player 
             playoff-teams-per-conference))
    (GET  ["/:id/conferences", :id #"[0-9]+"] [id] (get-conferences id))
    (POST "/conferences" [name tournamentid] (add-conference name tournamentid)))))
