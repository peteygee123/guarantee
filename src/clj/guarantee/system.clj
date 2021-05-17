(ns guarantee.system
  (:require [com.stuartsierra.component :as c]
            [clojure.edn :as edn]
            [guarantee.database :as database]
            [guarantee.cli :as cli]
            [clojure.java.io :as io]
            [guarantee.server :as server])
  (:gen-class))

(def config
  {:datomic {:uri "datomic:mem://guarantee"}
   :server nil})

(defn system
  [config-options]
  (prn :in ::system "System starting up")
  (c/system-map
   :datomic (database/component (:datomic config-options))
   :server (c/using (server/component config-options)
                    [:datomic])))

(defn cli-system
  [config-options]
  (prn :in ::system "System starting up")
  (c/system-map
   :cli (cli/component config-options)))

(defn start
  [sys]
  (prn :in ::start "Calling component start.")
  (c/start sys))

(defn run-cli
  [config-options]
  (let [sys (cli-system config-options)]
    (start sys)
    ))

(defn stop
  [sys]
  (c/stop sys))

(comment

  (start (system config))

  )
