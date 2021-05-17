(ns guarantee.time
  (:require [java-time :as java-time]
            [java-time.format :as jt-format]))

(def slash-format (jt-format/formatter "MM/dd/yyyy"))
(def dash-format (jt-format/formatter "MM-dd-yyyy"))
(def mdyyyy (jt-format/formatter "M/D/YYYY"))

(def utc-zone
  (java-time/with-clock (java-time/system-clock "UTC")
    (java-time/zone-id)))

(defn zone
  [zone]
  (java-time/with-clock (java-time/system-clock zone)
    (java-time/zone-id)))

(defn instant-formatter
  "Return a formated string from an instant"
  [formatter instant zone-id]
  (.format (.withZone formatter zone-id) (java-time/instant instant)))

(comment

  (instant-formatter mdyyyy (java.util.Date.) utc-zone)

  (java-time/instant (java.util.Date.))
  )

(defn string-to-instant-formatter
  [formatter date-string zone]
  (-> (java-time/local-date formatter date-string)
      (java-time/zoned-date-time zone)
      java-time/to-java-date))
