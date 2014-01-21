(ns valtweet.core
  (:require [clj-time.core :refer [now]]))

(defprotocol TweetStore
  (post [store username text time])
  (tweets-by [store username]))

(extend-type clojure.lang.PersistentHashSet
  TweetStore
  (post [store username text time]
    (conj store
          {:username username
           :text text
           :time time}))
  (tweets-by [store username]
    (->> store
         (filter (fn [tweet]
                   (= username
                      (:username tweet))))
         (sort-by :time)
         reverse)))
