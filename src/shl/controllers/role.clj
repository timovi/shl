(ns shl.controllers.role
  (:use [compojure.core])
  (:use [ring.util.response])
  (:require [clojure.string :as str]
            [ring.util.response :as ring]
            [clojure.data.json :as json]
            [shl.dao.user :as dao]))

(defn get-roles []
  (response (dao/get-roles)))

(defroutes app-routes
  (context "/roles" [] (defroutes role-routes
    (GET  "/" [] (get-roles)))))
