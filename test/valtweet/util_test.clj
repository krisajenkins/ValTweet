(ns valtweet.util-test
  (:require [clojure.test :refer :all]
            [valtweet.util :refer :all]
            [clj-time.core :refer [minus now seconds minutes hours days before?]]))

(deftest humanize-date-tests

  (let [start (now)]
    (are [date string] (= (humanize-date date)
                          string)

         start "Just now"

         (minus start (seconds 1)) "1 second ago"
         (minus start (seconds 2)) "2 seconds ago"
         (minus start (seconds 5)) "5 seconds ago"

         (minus start (minutes 1)) "1 minute ago"
         (minus start (minutes 2)) "2 minutes ago"
         (minus start (minutes 5)) "5 minutes ago"

         (minus start (hours 1)) "1 hour ago"
         (minus start (hours 2)) "2 hours ago"
         (minus start (hours 5)) "5 hours ago"

         (minus start (days 1)) "24 hours ago"
         (minus start (days 2)) "48 hours ago"

         )))
