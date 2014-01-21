(ns valtweet.ui
  (:gen-class :main true)
  (:require [clj-time.core :refer [now]]
            [clojure.java.io :refer [resource]]
            [clojure.string :refer [join]]
            [instaparse.core :as instaparse]
            [valtweet.core :refer :all]
            [valtweet.util :refer [humanize-date]]))

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

(defn -main
  [& args]
  (loop [system {:store #{}
                 :graph {}}]
    (print "> ")
    (flush)
    (if-let [input (read-line)]
      (let [[output new-system] (process-command (parse-command input)
                                                 system)]
        (when output
          (println (join "\n" output)))

        (recur new-system)))))
