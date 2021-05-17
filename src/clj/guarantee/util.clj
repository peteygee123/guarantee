(ns guarantee.util
  (:require [guarantee.time :as time]
            [datomic.api :as d]
            [clojure.string :as str]))

(defn map-vals
  "Maps over the values in a map and applies a function to them"
  [f x]
  (into {} (map (fn [[k v]]
                  [k (f v)])
                x)))

(defn map-keys
  "Maps over the keys in a map and applies a function to them"
  [f x]
  (into {} (map (fn [[k v]]
                  [(f k) v])
                x)))

(defn detect-delimiter
  "Detects delimiter in the file
  Note: Assumes that fields cannot contain any of the other possible delimiters. '\\|' or ',' or ' ' "
  [data]
  #_(re-find #"[^a-zA-Z\d\s:]" data)
  (cond
    (str/includes? data "|")  "\\|"
    (str/includes? data ",")  ","
    (str/includes? data " ")  " "
    :else nil))

(defn sanitize
  "Cleans and prepares the data to be transacted."
  [data]
  (-> (map-vals str/trim data)
      (assoc :person/id (d/squuid))
      ;; TODO: handle bogus date strings
      (update :person/birthday #(time/string-to-instant-formatter time/slash-format % time/utc-zone))))

(defn parse-data
  "Given a delimiter and a string of data, split into a vector"
  [delimiter data]
  (str/split data (re-pattern delimiter)))

(defn build-record
  "Given a vector, cooerce into a namespaced map. Assumes vector is ordered correctly"
  [data]
  (zipmap [:person/last-name :person/first-name :person/email :person/favorite-color :person/birthday]
          data))
