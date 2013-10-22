(ns shl.utils.time
  (:require [clj-time.format :as time]))

(def formatter (time/formatter "dd.MM.yyyy"))
