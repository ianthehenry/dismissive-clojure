(ns dismissive.incoming
  (:require [clj-time.core :as t]
            [clj-time.coerce :as coerce]
            [dismissive.db :refer [schedule-message]]))

(defn time-from-email [email]
  (->> 5 t/seconds (t/plus (email :at))))

(defn handle-incoming-email [email]
  (schedule-message (time-from-email email)
                    (email :subject)
                    (email :body))
  (println "scheduling email"))
