(ns valtweet.core-test
  (:require [clojure.test :refer :all]
            [valtweet.core :refer :all]
            [clj-time.core :refer [minus now minutes before?]]))

(defn wrap-setup
  [test-function]
  (let [fixed-now (clj-time.core/now)]
    (with-redefs [now (constantly fixed-now)]
      (test-function))))

(use-fixtures :once wrap-setup)

(deftest timeline-tests
  (let [store (-> #{}
                  (post (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
                  (post (->Tweet "Bob"   "Damn! We lost!"           (minus (now) (minutes 2))))
                  (post (->Tweet "Bob"   "Good game though."        (minus (now) (minutes 1)))))]
    (testing "Post and read"
      (is (= (map :text (tweets-by store "Alice"))
             ["I love the weather today"]))
      (is (= (map :text (tweets-by store "Bob"))
             ["Good game though."
              "Damn! We lost!"])))
    (testing "Timeline predicate"
      (is (= (map :text (timeline-matching store (fn [tweet]
                                                   (before? (minus (now) (minutes 3))
                                                            (:time tweet)))))
             ["Good game though."
              "Damn! We lost!"]))
      (is (empty? (timeline-matching store (fn [tweet]
                                             (before? (now)
                                                      (:time tweet)))))))))

(deftest followgraph-tests
  (testing "Follow"
    (let [graph (-> {}
                    (follow "Charlie" "Alice")
                    (follow "Charlie" "Bob")
                    (follow "Bob" "Charlie"))]
      (is (follows? graph "Charlie" "Alice"))
      (is (follows? graph "Charlie" "Bob"))
      (is (follows? graph "Bob" "Charlie"))
      (is (not (follows? graph "Bob" "Alice")))
      (is (not (follows? graph "Alice" "Steve")))
      (is (not (follows? graph "Steve" "Jim"))))))

(deftest wall-tests
  (let [tweet-alice-1   (->Tweet "Alice"   "I love the weather today" (minus (now) (minutes 5)))
        tweet-bob-1     (->Tweet "Bob"     "Damn! We lost!"           (minus (now) (minutes 8)))
        tweet-bob-2     (->Tweet "Bob"     "Good game though."        (minus (now) (minutes 1)))
        tweet-charlie-1 (->Tweet "Charlie" "It was so-so."            (minus (now) (minutes 10)))
        store (-> #{}
                  (post tweet-alice-1)
                  (post tweet-bob-1)
                  (post tweet-bob-2)
                  (post tweet-charlie-1))
        graph (-> {}
                  (follow "Charlie" "Alice")
                  (follow "Charlie" "Bob")
                  (follow "Bob" "Alice"))]

    (is (= (wall store graph "Alice")
           [tweet-alice-1]))
    (is (= (wall store graph "Bob")
           [tweet-bob-2
            tweet-alice-1
            tweet-bob-1]))
    (is (= (wall store graph "Charlie")
           [tweet-bob-2
            tweet-alice-1
            tweet-bob-1
            tweet-charlie-1]))
    (is (empty? (wall store graph "Steve")))))
