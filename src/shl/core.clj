(ns shl.core
  (:use [compojure.core]
        [ring.adapter.jetty :as ring])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [ring.util.response :as resp]
            [shl.conf :as conf]
            [shl.controllers.game :as game]
            [shl.controllers.player :as player]
            [shl.controllers.role :as role]
            [shl.controllers.statistics :as statistics]
            [shl.controllers.standings :as standings]
            [shl.controllers.team :as team]
            [shl.controllers.tournament :as tournament]
            [shl.controllers.user :as user])
  (:gen-class))

(defroutes app-routes
  (context "/shl" []
     (GET "/" [] (resp/redirect "index.html"))
     game/app-routes
     player/app-routes
     role/app-routes
     statistics/app-routes
     standings/app-routes
     team/app-routes
     tournament/app-routes
     user/app-routes
     (route/resources "/")
     (route/not-found "Not Found")))

(def application
  (-> (handler/site app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (middleware/wrap-json-params)))

(defn start [port]
  (run-jetty #'application {:port port :join? false}))

(defn -main []
  (let [port (:shl-api-port conf/conf)]
  (start port)))
