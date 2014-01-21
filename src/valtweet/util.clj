(ns valtweet.util
  (:require [clj-time.core :refer [now in-seconds interval seconds]]))

(defn humanize-date
  [date]

  (let [start (now)
        seconds (in-seconds (interval date start))
        minutes (/ seconds 60)
        hours (/ minutes 60)]
    (cond
     (= seconds 0) "Just now"
     (= seconds 1) "1 second ago"
     (< minutes 1) (format "%d seconds ago" seconds)

     (= minutes 1) "1 minute ago"
     (< hours 1) (format "%d minutes ago" minutes)

     (= hours 1) "1 hour ago"
     :else (format "%d hours ago" hours))))
