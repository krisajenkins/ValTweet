(ns valtweet.core-test
  (:require [clojure.test :refer :all]
            [valtweet.core :refer :all]))

(deftest a-test
  (testing "Posting"
    (-> (->MemoryTweetStore)
        (post "Alice" "I love the weather today")
        (post "Bob" "Damn! We lost!")
        (post "Bob" "Good game though."))))
