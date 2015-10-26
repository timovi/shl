(defproject shl "0.1.0-SNAPSHOT"
  :description "Solita Hockey League Scheduler"
  :url "https://github.com/timovi/shl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/java.jdbc  "0.3.0-alpha5"]
                 [honeysql "0.4.2"]
                 [postgresql "9.1-901.jdbc4"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [ring/ring-json "0.2.0"]
                 [compojure "1.1.3"]
                 [org.clojure/data.json "0.2.3"]
                 [clj-http "2.0.0"]
                 [clj-time "0.6.0"]]
  :main ^:skip-aot shl.core
  :uberjar-name "shl.jar"
  :profiles {:uberjar {:aot :all}})
