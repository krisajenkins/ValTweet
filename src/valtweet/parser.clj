(ns valtweet.parser
  (:require [clojure.java.io :refer [resource]]
            [instaparse.core :as instaparse]))

(def parser
  (instaparse/parser (resource "valtweet/commands.bnf")))

(defn parse-command
  [string]

  (->> (parser string)
       (instaparse/transform {:command identity
                              :follow-command (fn [username followers-username]
                                                {:command :follow
                                                 :username username
                                                 :follows-username followers-username})
                              :read-command (fn [username]
                                              {:command :read
                                               :username username})
                              :tweet-command (fn [username text]
                                               {:command :post
                                                :username username
                                                :text text})
                              :wall-command (fn [username]
                                              {:command :wall
                                               :username username})
                              :empty-command (fn []
                                               {:command :noop})})))
