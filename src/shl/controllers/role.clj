(ns shl.controllers.role
  (:use [compojure.core :only (defroutes GET)])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.user :as dao]))

(defn get-roles []
  (json/write-str (dao/get-roles)))

(defroutes routes
  (GET  "/roles/get.api" [] (get-roles)))

