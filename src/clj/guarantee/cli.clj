(ns guarantee.cli
  (:require [guarantee.database :as database]
            [guarantee.util :as util]
            [guarantee.time :as time]
            [com.stuartsierra.component :as c]
            [datomic.api :as d]
            ))

(defn- get-all-lines
  [file-path]
  (with-open [rdr (clojure.java.io/reader file-path)]
    (reduce conj [] (line-seq rdr))))

(defn- process-file
  "Fetches data from the file and returns a sanitized map"
  [file-path]
  (prn :in ::process-file :message "Processing file" :file-path file-path)
  (let [lines (get-all-lines file-path)
        delimiter (util/detect-delimiter (first lines))]
    (map (comp util/sanitize util/build-record #(util/parse-data delimiter %)) lines)))

;; Given an expected output, returns the corresponding sort-fn
(def output->sort-fn
  {1 #(sort-by (juxt :person/favorite-color :person/last-name) %)
   2 #(sort-by :person/birthday %)
   3 #(reverse (sort-by :person/last-name %))})

(defn- shape-response
  [record]
  (update record :person/birthday #(time/instant-formatter time/mdyyyy % time/utc-zone)))

(defn process-files
  "Given config,"
  [output files]
  ;; We do no validation on the expected output
  (let [sort-fn (get output->sort-fn output)
        files (remove nil? (vals files))
        file-data (apply concat (for [file files]
                                  (process-file file)))]
    (clojure.pprint/pprint (map shape-response (sort-fn file-data)))))

(defrecord Cli [config]
  c/Lifecycle
  (start [this]
    (process-files (:output this) (:files this)))
  (stop [this]
    this))

(defn component
  [cfg]
  (map->Cli cfg))
