(ns shl.controllers.conference
  (:use [compojure.core :only (defroutes GET POST)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.conference :as dao]))

(defn get-conferences [tournamentid]
  (json/write-str (dao/get-conferences (Integer/parseInt tournamentid))))

(defn add [name tournamentid]
  (when-not (and (str/blank? name) 
                 (str/blank? tournamentid))
    (json/write-str (dao/add-conference name (Integer/parseInt tournamentid))
  (true))))

(defroutes routes
  (GET  "/conferences/get.api" [tournamentid] (get-conferences tournamentid))
  (POST "/conferences/add.api" [name tournamentid] (add name tournamentid)))

