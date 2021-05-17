(ns guarantee.server
  (:require [com.stuartsierra.component :as c]
            [muuntaja.core :as m]
            [muuntaja.format.json :as json-format]
            [muuntaja.middleware :as muunt-middleware]
            [org.httpkit.server :as server]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.middleware.params :as params]
            [guarantee.handlers.record :as record]
            [guarantee.middleware :as middleware]))

(def ^:private muuntaja-opts
  (-> m/default-options
      (update :formats select-keys ["application/transit+json" "application/json"])
      (update
       :formats merge
       {"application/json" json-format/format})
      (assoc :default-format "application/json")))

(defn app
  [services]
  (ring/ring-handler
    (ring/router
     [["/healthz" {:get (fn [{{:strs [x y]} :query-params :as req}]
                          {:status 200
                           :body {:status :OK}})}]
      ["/records" {:post record/create-record}]
      ["/records/:attr" {:get record/get-records}]]
      {:data {:muuntaja m/instance
      	      :middleware [params/wrap-params
                           [muunt-middleware/wrap-format muuntaja-opts]
                           [middleware/wrap-services services]
                           muuntaja/format-middleware
                           rrc/coerce-exceptions-middleware
                           rrc/coerce-request-middleware
                           rrc/coerce-response-middleware]}})
    (ring/create-default-handler)))


(defrecord ApiServer [config httpkit datomic]
  c/Lifecycle
  (start [this]
    (prn :in ::api :message "starting api server")
    (if httpkit
      this
      (assoc this :httpkit
             (server/run-server (app {:datomic datomic}) {:port 4500}))))
  (stop [this]
    (prn :in :api "stopping api server")
    (when httpkit
      (httpkit))
    (assoc this :httpkit nil)))

(defn component
  "Constructor function for a new mongo db"
  [cfg]
  (map->ApiServer {:config (:server cfg)}))
