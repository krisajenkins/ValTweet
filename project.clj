(defproject valtweet "0.1.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [midje "1.6.0"]
                 [clj-time "0.6.0"]
                 [instaparse "1.2.14"]]
  :profiles {:dev {:plugins [[lein-midje "3.1.1"]]}}
  :main valtweet.ui)
