(ns valtweet.core-test
  (:require [clj-time.core :refer [before? minus minutes now]]
            [midje.sweet :refer :all]
            [valtweet.core :refer :all]))

(background
 (around :facts
         (let [fixed-now (clj-time.core/now)]
           (with-redefs [now (constantly fixed-now)]
             ?form))))

(facts timeline-tests
  (let [store (-> #{}
                  (post (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
                  (post (->Tweet "Bob"   "Damn! We lost!"           (minus (now) (minutes 2))))
                  (post (->Tweet "Bob"   "Good game though."        (minus (now) (minutes 1)))))]
    (fact "Post and read"
      (map :text (tweets-by store "Alice")) => ["I love the weather today"]
      (map :text (tweets-by store "Bob"))   => ["Good game though."
                                                "Damn! We lost!"])
    (fact "Timeline predicate"
      (map :text (timeline-matching store (fn [tweet]
                                            (before? (minus (now) (minutes 3))
                                                     (:time tweet)))))
      => ["Good game though."
          "Damn! We lost!"]

      (timeline-matching store (fn [tweet]
                                 (before? (now)
                                          (:time tweet)))) => empty?)))

(facts followgraph-tests
  (fact "Follow"
    (let [graph (-> {}
                    (follow "Charlie" "Alice")
                    (follow "Charlie" "Bob")
                    (follow "Bob" "Charlie"))]
      (follows? graph "Charlie" "Alice") => true
      (follows? graph "Charlie" "Bob") => true
      (follows? graph "Bob" "Charlie") => true
      (follows? graph "Bob" "Alice") => false
      (follows? graph "Alice" "Steve") => false
      (follows? graph "Steve" "Jim")))) => false

(facts wall-tests
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

    (wall store graph "Alice")   => [tweet-alice-1]
    (wall store graph "Bob")     => [tweet-bob-2
                                     tweet-alice-1
                                     tweet-bob-1]
    (wall store graph "Charlie") => [tweet-bob-2
                                     tweet-alice-1
                                     tweet-bob-1
                                     tweet-charlie-1]
    (wall store graph "Steve")   => empty?))
