(ns valtweet.formatters
  (:require [clj-time.core :refer [in-seconds interval now]]))

(defn humanize-date
  [date]

  (let [start (now)
        seconds (in-seconds (interval date start))
        minutes (int (/ seconds 60))
        hours   (int (/ minutes 60))]
    (cond
     (zero? seconds) "Just now"
     (= seconds 1) "1 second ago"
     (< minutes 1) (format "%d seconds ago" seconds)

     (= minutes 1) "1 minute ago"
     (< hours 1) (format "%d minutes ago" minutes)

     (= hours 1) "1 hour ago"
     :else (format "%d hours ago" hours))))

(defn format-tweet
  ([{:keys [username text time]} & {include-username? :include-username?
                                    :or {:include-username? false}}]
     (if include-username?
       (format "%s - %s (%s)"
               username
               text
               (humanize-date time))
       (format "%s (%s)"
               text
               (humanize-date time)))))
