(ns dismissive.db
  (:require [korma.core :as sql]
            [korma.db :refer [defdb sqlite3]]
            [clj-time.core :as t]
            [clj-time.coerce :as coerce]
            [dismissive.config :refer [db-path]]))

(defdb dismissive-db (sqlite3 {:subname db-path}))

(sql/defentity messages)

(defn schedule-message [at subject body]
  (sql/insert messages
            (sql/values {:subject subject
                         :body body
                         :at (coerce/to-long at)
                         :completed 0})))

(defn incomplete-messages-before [date]
  (sql/select messages
            (sql/where {:completed 0
                        :at [< (coerce/to-long date)]})))

(defn complete-message [message]
  (sql/update messages
              (sql/where {:id (:id message)})
              (sql/set-fields {:completed 1})))
