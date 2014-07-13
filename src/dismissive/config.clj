(ns dismissive.config
  (:require [carica.core :as carica]))

(def config (carica/configurer (carica/resources "prod.edn")))
(def override-config (carica/overrider config))

(def mandrill-key (config :mandrill-key))
(def db-path "dismissive.db")
