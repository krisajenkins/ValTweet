(ns valtweet.util-test
  (:require [clojure.test :refer :all]
            [valtweet.util :refer :all]
            [clj-time.core :refer [minus now seconds minutes hours days before?]]))

(defn wrap-setup
  [test-function]
  (let [fixed-now (clj-time.core/now)]
    (with-redefs [now (constantly fixed-now)]
      (test-function))))

(use-fixtures :once wrap-setup)

(deftest humanize-date-tests
  (are [date string] (= (humanize-date date)
                        string)

       (now) "Just now"

       (minus (now) (seconds 1)) "1 second ago"
       (minus (now) (seconds 2)) "2 seconds ago"
       (minus (now) (seconds 5)) "5 seconds ago"

       (minus (now) (minutes 1)) "1 minute ago"
       (minus (now) (minutes 2)) "2 minutes ago"
       (minus (now) (minutes 5)) "5 minutes ago"

       (minus (now) (hours 1)) "1 hour ago"
       (minus (now) (hours 2)) "2 hours ago"
       (minus (now) (hours 5)) "5 hours ago"

       (minus (now) (days 1)) "24 hours ago"
       (minus (now) (days 2)) "48 hours ago"))
