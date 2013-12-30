(ns shl.utils.time
  (:require [clj-time.format :as time-format]
            [clj-time.coerce :as time-coerce]))

(def formatter (time-format/formatter "dd.MM.yyyy"))

(defn from-sql-date [date]
  (if (nil? date) nil (time-coerce/from-sql-date date)))
