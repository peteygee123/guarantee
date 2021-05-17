(ns guarantee.database
  (:require [com.stuartsierra.component :as c]
            [datomic.api :as d]))

(def schema
  ;; TODO: we might want to index fields
  [{:db/id #db/id[:db.part/db]
    :db/ident :person/id
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :person/last-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :person/first-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :person/favorite-color
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :person/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id[:db.part/db]
    :db/ident :person/birthday
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}])

(defrecord Datomic [uri db conn]
  c/Lifecycle
  (start [this]
    (let [_ (d/create-database uri)
          conn (d/connect uri)
          db (d/db conn)]
      @(d/transact conn schema)
      (prn :in ::datomic :message "Connected to datomic")
      (assoc this :db db :conn conn)))
  (stop [this]
    (when-let [conn (:conn this)]
      (prn :in ::datomic :message "Closing Datomic connection" conn)
      (assoc this :db nil :conn nil))
    this))

(defn component
  [cfg]
  (map->Datomic cfg))
