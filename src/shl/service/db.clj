(ns shl.service.db)

;(def db {:subprotocol (System/getenv "DB_TYPE")
;         :subname (System/getenv "DB_URL")
;         :user (System/getenv "DB_USER")
;         :password (System/getenv "DB_PASS")})

(def db {:subprotocol "postgresql" 
         :subname "//localhost:5432/shl" 
         :user "shl" 
         :password "shl"})
