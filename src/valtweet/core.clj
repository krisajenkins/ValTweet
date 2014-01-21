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

(defprotocol FollowGraph
  (follow [graph user-a follows-user-b])
  (follows? [graph user-a follows-user-b]))

(extend-type clojure.lang.PersistentArrayMap
  FollowGraph
  (follow [graph user-a follows-user-b]
    (update-in graph [user-a]
               (fn [followers]
                 (conj (or followers #{})
                       follows-user-b))))
  (follows? [graph user-a follows-user-b]
    (let [following (graph user-a)]
      (contains? following follows-user-b))))
