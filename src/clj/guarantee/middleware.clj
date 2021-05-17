(ns guarantee.middleware
  (:require [datomic.api :as d]))

(defn wrap-services
  [handler services]
  (fn [request]
    (let [datomic (:datomic services)
          conn (:conn datomic)
          services (assoc-in services [:datomic :db] (d/db conn))]
      (handler (assoc request :services services)))))
