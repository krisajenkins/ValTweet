(ns valtweet.commands
  (:require [clj-time.core :refer [now]]
            [valtweet.core :refer :all]
            [valtweet.formatters :refer :all]))

(defmulti process-command
  (fn [{command :command} system]
    command))

(defmethod process-command :post
  [{:keys [username text]} system]
  [nil (update-in system [:store]
                  post (->Tweet username text (now)))])

(defmethod process-command :read
  [{:keys [username text]} system]
  [(map format-tweet
        (tweets-by (:store system) username))
   system])

(defmethod process-command :wall
  [{:keys [username text]} system]
  [(map #(format-tweet %
                       :include-username? true)
        (wall (:store system) (:graph system) username))
   system])

(defmethod process-command :follow
  [{:keys [username follows-username]} system]
  [nil (update-in system [:graph]
                  follow
                  username
                  follows-username)])

(defmethod process-command :noop
  [command system]
  [nil system])
