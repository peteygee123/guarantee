(ns user
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as c]
            [org.httpkit.client :as http]
            [guarantee.system :as system]))

(def system nil)

(def config
  {:datomic {:uri "datomic:mem://guarantee"}
   :server nil})

(defn init
  []
  (alter-var-root #'system
                  (constantly (system/system config))))

(defn start
  []
  (alter-var-root #'system system/start))

(defn stop
  []
  (alter-var-root #'system
                  (fn [s]
                    (when s (system/stop s)))))

(defn go
  []
  (init)
  (start))

(defn reset
  []
  (stop)
  (repl/refresh :after 'user/go))

(comment

  (go)
  (stop)
  (reset)
  )
