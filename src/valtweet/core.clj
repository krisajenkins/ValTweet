(ns valtweet.core
  (:require [clj-time.core :refer [now]]))

(defrecord Tweet
    [username text time])

(defprotocol TweetStore
  (post [store tweet])
  (tweets-by [store username]))

(extend-type clojure.lang.PersistentHashSet
  TweetStore
  (post [store tweet]
    (conj store tweet))
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

(defn wall
  [store graph username]
  (->> store
       (filter (fn [tweet]
                 (or (= username (:username tweet))
                     (follows? graph username (:username tweet)))))
       (sort-by :time)
       reverse))
