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

(defn activate-tournament [tournamentid]
  (when-not (str/blank? tournamentid)
    (dao/inactivate-tournaments)
    (dao/activate-tournament (Integer/parseInt tournamentid))
    (response (dao/get-tournament (Integer/parseInt tournamentid)))))

(defn get-tournaments []
  (response (dao/get-tournaments)))

(defn add [name startdate enddate playoff-teams-per-conference]
  (when-not (and (str/blank? name) 
                 (str/blank? enddate)
                 (str/blank? playoff-teams-per-conference))
    (dao/add-tournament name 
                        (time/parse time-utils/formatter startdate) 
                        (time/parse time-utils/formatter enddate) 
                        (Integer/parseInt playoff-teams-per-conference))
    (response (dao/get-tournament-by-name name))))

(defn get-conferences [tournamentid]
  (response (dao/get-conferences (Integer/parseInt tournamentid))))

(defn add-conference [name tournamentid games-per-player]
  (when-not (and (str/blank? name) 
                 (str/blank? tournamentid)
                 (str/blank? games-per-player))
    (dao/add-conference name (Integer/parseInt tournamentid) (Integer/parseInt games-per-player)))
  (response (dao/get-conference name (Integer/parseInt tournamentid))))

(defroutes app-routes
  (context "/tournaments" [] (defroutes tournament-routes
    (GET "/active/" [] (get-active))
    (POST "/activate/" [tournamentid] (activate-tournament tournamentid))
    (GET "/" [] (get-tournaments))
    (POST "/" [name startdate enddate playoff-teams-per-conference] 
      (add name startdate enddate playoff-teams-per-conference))
    (GET  ["/:id/conferences/", :id #"[0-9]+"] [id] (get-conferences id))
    (POST "/conferences/" [name tournamentid games-per-player] 
      (add-conference name tournamentid games-per-player)))))
