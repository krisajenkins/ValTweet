(defproject valtweet "0.1.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [expectations "1.4.56"]
                 [clj-time "0.6.0"]
                 [instaparse "1.2.14"]]
  :profiles {:dev {:plugins [[lein-expectations "0.0.7"]
                             [lein-autoexpect "1.0"]]}}
  :main valtweet.ui)
