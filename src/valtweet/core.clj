(ns valtweet.core)

(defrecord Tweet
    [username text time])

(defprotocol TweetStore
  (post [store tweet])
  (timeline-matching [store predicate])
  (tweets-by [store username]))

(extend-type clojure.lang.PersistentHashSet
  TweetStore
  (post [store tweet]
    (conj store tweet))
  (timeline-matching [store predicate]
    (->> store
         (filter predicate)
         (sort-by :time)
         reverse))
  (tweets-by [store username]
    (timeline-matching store
                       (fn [tweet]
                         (= username
                            (:username tweet))))))

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
  (timeline-matching store
                     (fn [tweet]
                       (or (= username (:username tweet))
                           (follows? graph username (:username tweet))))))
