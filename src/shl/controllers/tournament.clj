(ns shl.controllers.tournament
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.service.conference :as service]))

(defn get-active []
  (json/write-str (service/get-active-tournament)))

(defn add [name startdate enddate games-per-player 
           playoff-teams-per-conference]
  (when-not (and (str/blank? name) 
                 (str/blank? enddate) 
                 (str/blank? games-per-player)
                 (str/blank? playoff-teams-per-conference))
    (service/add-tournament name startdate enddate games-per-player 
                            playoff-teams-per-conference))
  (ring/redirect "/"))

(defroutes routes
  (GET  "/tournament/get.api" [] (get-active))
  (POST "/tournament/add.api" [name startdate enddate games-per-player 
                               playoff-teams-per-conference] 
        (add name startdate enddate games-per-player 
             playoff-teams-per-conference)))

