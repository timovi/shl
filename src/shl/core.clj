(ns shl.core
  (:use [compojure.core :only (defroutes)]
        [ring.adapter.jetty :as ring])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [shl.controllers.conference :as conference]
            [shl.controllers.tournament :as tournament]))

(defroutes routes
  conference/routes
  tournament/routes
  (route/resources "/"))
  ;(route/not-found (layout/four-oh-four)))

(def application (handler/site routes))

(defn start [port]
  (run-jetty #'application {:port port :join? false}))

(defn -main []
  (let [port (Integer/parseInt 
               (or (System/getenv "PORT") "8080"))]
  (start port)))
