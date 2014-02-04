(ns valtweet.integration-test
  (:require [expectations :refer :all]
            [clj-time.core :refer [minus minutes now seconds]]
            [valtweet.commands :refer [process-command]]
            [valtweet.parser :refer [parse-command]]))

(let [start-time (now)]
  (dorun
   (reduce
    (fn [system [input time-offset expected-output]]
      (with-redefs [now (fn [] (minus start-time time-offset))]
        (let [parsed-command (parse-command input)
              [output new-system] (process-command parsed-command system)]
          (expect output expected-output)
          new-system)))
    {:graph {}
     :store #{}}
    (partition 3
               ["Alice -> I love the weather today" (minutes 5) nil
                "Bob -> Damn! We lost!"             (minutes 2) nil
                "Bob -> Good game though."          (minutes 1) nil

                ""                                  (minutes 1) nil

                "Alice"                             (minutes 0) ["I love the weather today (5 minutes ago)"]
                "Bob"                               (minutes 0) ["Good game though. (1 minute ago)"
                                                                 "Damn! We lost! (2 minutes ago)"]

                "Charlie -> I'm in New York today! Anyone wants to have a coffee?" (seconds 2) nil
                "Charlie follows Alice"                                            (seconds 0) nil
                "Charlie wall"                                                     (seconds 0) ["Charlie - I'm in New York today! Anyone wants to have a coffee? (2 seconds ago)"
                                                                                                "Alice - I love the weather today (5 minutes ago)"]

                "Charlie follows Bob" (seconds 0) nil
                "Charlie wall"        (seconds 0) ["Charlie - I'm in New York today! Anyone wants to have a coffee? (2 seconds ago)"
                                                   "Bob - Good game though. (1 minute ago)"
                                                   "Bob - Damn! We lost! (2 minutes ago)"
                                                   "Alice - I love the weather today (5 minutes ago)"]]))))
