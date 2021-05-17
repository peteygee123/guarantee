(ns guarantee.handlers.record
  (:require [clojure.string :as str]
            [datomic.api :as d]
            [ring.util.response :as ring-response]
            [guarantee.time :as time]
            [guarantee.util :as util]))

(defn add-derived-fields
  [record]
  ;; Doesn't take into account if a person doesn't have a first / last name
  (assoc record :name (->> ((juxt :first-name :last-name) record)
                           (str/join " "))))

(defn shape-response
  "Responsible for taking an entity from the database and formatting the response"
  [record]
  (->> record
       (util/map-keys (comp keyword name))
       add-derived-fields))

(defn create-record
  [{{:keys [data] :as body-params} :body-params {{:keys [conn]} :datomic} :services :as req}]
  (prn :in ::create-record :message "Creating record" :data data)
  (let [delimiter (util/detect-delimiter data)
        record (util/build-record (util/parse-data delimiter data))
        sanitized-record (util/sanitize record)
        {:keys [db-after tempids]} @(d/transact conn [sanitized-record])
        record (d/entity db-after (first (vals tempids)))]
    {:status 201
     ;; location
     :body (shape-response record)}))


(defn- all-records
  [db]
  (->> (d/q '[:find ?e
              :where
              [?e :person/first-name]]
            db)
       (map #(d/entity db (first %)))))

(def attr->schema
  {:birthdate :birthday
   :color :favorite-color
   :name :last-name})

(defn get-records
  [{{:keys [data] :as body-params} :body-params {:keys [attr]} :path-params {{:keys [conn db]} :datomic}
    :services :as req}]
  (prn :in ::get-records :message "Get Records" :sort-attr attr)
  ;; TODO: validate we are only allowing valid sorts
  (let [records (all-records db)
        ;; We are not doing any validation on the sort-attr
        sort-attr (get attr->schema (keyword attr))]
    (ring-response/response (->> (map shape-response records)
                                 (sort-by sort-attr)))))
