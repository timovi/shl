(ns shl.controllers.statistics
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [clj-time.format :as time]
            [shl.utils.time :as time-utils]
            [shl.service.statistics :as service]))

(defn get-conference-statistics [conferenceid]
  (response (service/get-conference-stats (Integer/parseInt conferenceid))))

(defn get-player-statistics [playerid]
  (response (service/get-player-stats (Integer/parseInt playerid))))

(defroutes app-routes
  (context "/statistics" [] (defroutes statistics-routes
    (GET ["/conference/:id/" :id #"[0-9]+"] [id] (get-conference-statistics id))
    (GET ["/player/:id/" :id #"[0-9]+"] [id] (get-player-statistics id)))))