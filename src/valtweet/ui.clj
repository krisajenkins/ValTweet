(ns valtweet.ui
  (:require [valtweet.util :refer [humanize-date]]
            [clojure.java.io :refer [resource]]
            [instaparse.core :as instaparse]))

(defn format-tweet
  [{:keys [username text time]}]
  (format "%s - %s (%s)"
          username
          text
          (humanize-date time)))

(def parser
  (instaparse/parser (resource "valtweet/commands.bnf")))

(defn parse-command
  [string]

  (->> (parser string)
       (instaparse/transform {:username identity
                              :freetext identity
                              :command identity
                              :follow-command (fn [username _ _ _ followers-username]
                                                {:command :follow
                                                 :username username
                                                 :follows-username followers-username})
                              :read-command (fn [username]
                                                {:command :read
                                                 :username username})
                              :tweet-command (fn [username _ _ _ text]
                                                {:command :post
                                                 :username username
                                                 :text text})
                              :wall-command (fn [username _ _]
                                                {:command :wall
                                                 :username username})
                              })))
