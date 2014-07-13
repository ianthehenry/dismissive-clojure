(ns dismissive.outgoing
  (:require [org.httpkit.client :as http]
            [chime :refer [chime-ch chime-at]]
            [clj-time.core :as t]
            [clj-time.coerce :as coerce]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.core.async :as async :refer [<! >! >!! go chan go-loop]]
            [clojure.data.json :as json]
            [dismissive.config :refer [mandrill-key]]
            [dismissive.db :refer [incomplete-messages-before complete-message]]))

(defn send-email [to subject body reply-to]
  (let [message {:auto_html false
                 :to [{:email to}]
                 :from_name "Secret Admirer"
                 :from_email "somethinginteresting@example.com"
                 :headers {"Reply-To" reply-to}
                 :subject subject
                 :text body}]
    (http/post "https://mandrillapp.com/api/1.0/messages/send"
               {:body (json/write-str {:message message
                                       :key mandrill-key})})))

(defonce outgoing-mail-ch (chan))

(defn check-scheduled [before]
  (println "checking")
  (doseq [message (incomplete-messages-before before)]
    (println "scheduling")
    (>!! outgoing-mail-ch message)))

(go-loop []
         (let [message (<! outgoing-mail-ch)]
           @(send-email "ianthehenry@gmail.com"
                        (:subject message)
                        (:body message)
                        "noreply@example.org")
           (complete-message message)
           (recur)))

(defn new-scheduler []
  (chime-at (periodic-seq (t/now) (-> 5 t/seconds))
            check-scheduled))

(defonce current-scheduler (atom nil))

(defn start-scheduling []
  (if @current-scheduler
    (println "Already scheduling!")
    (do
      (reset! current-scheduler (new-scheduler))
      (println "Starting"))))

(defn stop-scheduling []
  (if-let [scheduler @current-scheduler]
    (do
      (scheduler)
      (reset! current-scheduler nil)
      (println "Stopped"))
    (println "Not currently scheduling!")))

#_ (stop-scheduling)
#_ (start-scheduling)
