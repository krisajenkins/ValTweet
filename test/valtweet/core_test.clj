(ns valtweet.core-test
  (:require [clojure.test :refer :all]
            [valtweet.core :refer :all]
            [clj-time.core :refer [minus now minutes]]))

(deftest timeline-tests
  (testing "Post and read"
    (let [store (-> #{}
                    (post "Alice" "I love the weather today" (minus (now) (minutes 5)))
                    (post "Bob" "Damn! We lost!"             (minus (now) (minutes 2)))
                    (post "Bob" "Good game though."          (minus (now) (minutes 1))))]
      (is (= (map :text (tweets-by store "Alice"))
             ["I love the weather today"]))
      (is (= (map :text (tweets-by store "Bob"))
             ["Good game though."
              "Damn! We lost!"])))))
