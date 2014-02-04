(ns valtweet.formatters-test
  (:require [clj-time.core :refer [days hours minus minutes now seconds]]
            [midje.sweet :refer :all]
            [valtweet.core :refer :all]
            [valtweet.formatters :refer :all]))

(background
 (around :facts
         (let [fixed-now (clj-time.core/now)]
           (with-redefs [now (constantly fixed-now)]
             ?form))))

(facts humanize-date-tests
  (tabular
      (fact "Humanize date tests."
        (humanize-date ?date) => ?string)
      ?date ?string

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

(facts format-tweet-test
  (fact "Formatting."
    (format-tweet (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
    => "I love the weather today (5 minutes ago)"
    (format-tweet (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5)))
                  :include-username? true)
    => "Alice - I love the weather today (5 minutes ago)"))
