(ns dismissive.main
  (:use [org.httpkit.server :only [run-server]])
  (:require [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response]]
            [clojure.string :refer [trim]]
            [clj-time.coerce :as coerce]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.data.json :as json]
            [dismissive.outgoing :refer [start-scheduling stop-scheduling]]
            [dismissive.incoming :refer [handle-incoming-email]]))

(defn rename-keys [m keymap]
  (into {}
        (for [[oldname newname] keymap] [newname (get m oldname)])))

(defn useful-from-mandrill [mandrill-message]
  (let [ts (get mandrill-message "ts")
        useful-fields (-> mandrill-message
                          (get "msg")
                          (select-keys ["email" "text" "subject"])
                          (rename-keys {"email" :to "text" :body "subject" :subject}))]
    (-> useful-fields
        (assoc :at (-> ts (* 1000) coerce/from-long))
        (update-in [:body] trim))))

(defn incoming-mandrill-events [req]
  (let [events (-> req
                   :params
                   (get "mandrill_events")
                   json/read-str)]
    (->> events
         (filter #(= (get % "event") "inbound"))
         (map useful-from-mandrill))))

(defroutes app-routes
  (GET "/" [] "hello world")
  (POST "/" req
        (doseq [incoming (incoming-mandrill-events req)]
          (handle-incoming-email incoming))
        (response {:res "ok"}))
  (route/not-found "Not Found"))

(def app (-> #'app-routes
             wrap-params
             wrap-json-response))

(defonce server (atom nil))

(defn start-server [port]
  (if @server
    (println "server already running")
    (do
      (reset! server (run-server app {:port port}))
      (println "server running on port " port))))

(defn stop-server []
  (if @server
    (do
      (@server)
      (reset! server nil)
      (println "server stopped"))
    (println "server not running")))

(defn -main [& args]
  (start-server 8080))

#_ (stop-server)
#_ (start-server 8080)

#_ (stop-scheduling)
#_ (start-scheduling)
