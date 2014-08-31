(ns shl.core
  (:use [compojure.core :only (defroutes)]
        [ring.adapter.jetty :as ring])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [shl.controllers.game :as game]
            [shl.controllers.player :as player]
            [shl.controllers.role :as role]
            [shl.controllers.statistics :as statistics]
            [shl.controllers.standings :as standings]
            [shl.controllers.team :as team]
            [shl.controllers.tournament :as tournament]
            [shl.controllers.user :as user]))

(defroutes app-routes
  game/app-routes
  player/app-routes
  role/app-routes
  statistics/app-routes
  standings/app-routes
  team/app-routes
  tournament/app-routes
  user/app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def application
  (-> (handler/site app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))

(defn start [port]
  (run-jetty #'application {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt 
               (or (System/getenv "SHL_API_PORT") "8080"))]
  (start port)))
