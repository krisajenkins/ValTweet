(ns valtweet.core)

(defrecord MemoryTweetStore [])

(defprotocol TweetStore
  (post [store username text]))

(extend-type MemoryTweetStore
  TweetStore
  (post [store username text]
    store))
