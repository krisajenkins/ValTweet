(ns valtweet.core-test
  (:require [clojure.test :refer :all]
            [valtweet.core :refer :all]
            [clj-time.core :refer [minus now minutes]]))

(deftest timeline-tests
  (testing "Post and read"
    (let [store (-> #{}
                    (post (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
                    (post (->Tweet "Bob"   "Damn! We lost!"           (minus (now) (minutes 2))))
                    (post (->Tweet "Bob"   "Good game though."        (minus (now) (minutes 1)))))]
      (is (= (map :text (tweets-by store "Alice"))
             ["I love the weather today"]))
      (is (= (map :text (tweets-by store "Bob"))
             ["Good game though."
              "Damn! We lost!"])))))

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
