(ns valtweet.ui
  (:require [clojure.string :refer [join]]
            [valtweet.commands :refer :all]
            [valtweet.parser :refer :all])
  (:gen-class :main true))

(defn -main
  [& args]
  (loop [system {:store #{}
                 :graph {}}]
    (print "> ")
    (flush)
    (if-let [input (read-line)]
      (let [[output new-system] (process-command (parse-command input)
                                                 system)]
        (when output
          (println (join "\n" output)))

        (recur new-system)))))
