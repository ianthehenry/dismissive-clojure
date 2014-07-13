(defproject dismissive "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [ring/ring-core "1.3.0-RC1"]
                 [ring/ring-json "0.3.1"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.6"]
                 [korma "0.3.2"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [clj-time "0.7.0"]
                 [jarohen/chime "0.1.6"]
                 [org.clojure/data.json "0.2.4"]
                 [sonian/carica "1.1.0" :exclusions [[cheshire]]]]
  :profiles {:dev {:dependencies [[ring/ring-devel "1.3.0-RC1"]
                                  [javax.servlet/servlet-api "2.5"]]}}
  :main dismissive.main)
