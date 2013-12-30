(ns shl.controllers.statistics
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [clj-time.format :as time]
            [shl.utils.time :as time-utils]
            [shl.service.statistics :as service]))

(defn get-tournament-standings [tournamentid]
  (response (service/get-tournament-standings (Integer/parseInt tournamentid))))

(defn get-conference-standings [conferenceid]
  (response (service/get-conference-standings (Integer/parseInt conferenceid))))

(defn get-player-standings [playerid]
  (response (service/get-player-standings (Integer/parseInt playerid))))

(defroutes app-routes
  (context "/stats" [] (defroutes stats-routes
    (GET ["/tournament/:id" :id #"[0-9]+"] [id] (get-tournament-standings id))
    (GET ["/conference/:id" :id #"[0-9]+"] [id] (get-conference-standings id))
    (GET ["/player/:id" :id #"[0-9]+"] [id] (get-player-standings id)))))