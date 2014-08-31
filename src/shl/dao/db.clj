(ns shl.dao.db)

(def db {:subprotocol (or (System/getenv "SHL_DB_TYPE") "postgresql")
         :subname     (or (System/getenv "SHL_DB_URL")  "//localhost:5432/shl")
         :user        (or (System/getenv "SHL_DB_USER") "shl")
         :password    (or (System/getenv "SHL_DB_PASS") "shl")})

