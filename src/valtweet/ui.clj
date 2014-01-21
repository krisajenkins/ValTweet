(ns valtweet.ui
  (:require [valtweet.util :refer [humanize-date]]))

(defn format-tweet
  [{:keys [username text time]}]
  (format "%s - %s (%s)"
          username
          text
          (humanize-date time)))
