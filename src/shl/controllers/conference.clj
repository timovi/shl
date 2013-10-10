(ns shl.controllers.conference
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.service.conference :as service]))

(defn get-conferences [tournamentid]
  (json/write-str (service/get-conferences (Integer/parseInt tournamentid))))

(defn add [name tournamentid]
  (when-not (and (str/blank? name) 
               (str/blank? tournamentid))
    (json/write-str (service/add-conference name (Integer/parseInt tournamentid))
  (ring/redirect "/"))))

(defroutes routes
  (GET  "/conferences/get.api" [tournamentid] (get-conferences tournamentid))
  (POST "/conferences/add.api" [name tournamentid] (add name tournamentid)))

