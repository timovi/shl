(ns shl.controllers.tournament
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [clj-time.format :as time]
            [shl.utils.time :as time-utils]
            [shl.dao.conference :as dao]))

(defn get-active []
  (json/write-str (dao/get-active-tournament)))

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
  (true))

(defroutes routes
  (GET  "/tournament/get.api" [] (get-active))
  (POST "/tournament/add.api" [name startdate enddate games-per-player 
                               playoff-teams-per-conference] 
        (add name startdate enddate games-per-player 
             playoff-teams-per-conference)))

