(ns valtweet.ui-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [valtweet.core :refer :all]
            [valtweet.parser :refer :all]
            [valtweet.ui :refer :all]
            [clj-time.core :refer [minus now seconds minutes before?]]))

(background
 (around :facts
         (let [fixed-now (clj-time.core/now)]
           (with-redefs [now (constantly fixed-now)]
             ?form))))

(defn wrap-setup
  [test-function]
  (let [fixed-now (clj-time.core/now)]
    (with-redefs [now (constantly fixed-now)]
      (test-function))))

(use-fixtures :once wrap-setup)

(facts format-tweet-test
  (fact "Formatting."
    (format-tweet (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
    => "I love the weather today (5 minutes ago)"
    (format-tweet (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5)))
                  :include-username? true)
    => "Alice - I love the weather today (5 minutes ago)"))

(deftest end-to-end-test
  (let [start-time (now)]
    (dorun
     (reduce
      (fn [system [input time-offset expected-output]]
        (with-redefs [now (fn [] (minus start-time time-offset))]
          (let [parsed-command (parse-command input)
                [output new-system] (process-command parsed-command system)]
            (is (= output expected-output))
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
                  "Charlie wall"                                                     (seconds 0)
                  ["Charlie - I'm in New York today! Anyone wants to have a coffee? (2 seconds ago)"
                   "Alice - I love the weather today (5 minutes ago)"]

                  "Charlie follows Bob" (seconds 0) nil
                  "Charlie wall"        (seconds 0) ["Charlie - I'm in New York today! Anyone wants to have a coffee? (2 seconds ago)"
                                                     "Bob - Good game though. (1 minute ago)"
                                                     "Bob - Damn! We lost! (2 minutes ago)"
                                                     "Alice - I love the weather today (5 minutes ago)"]])))))
