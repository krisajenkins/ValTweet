(ns valtweet.core-test
  (:require [clj-time.core :refer [before? minus minutes now]]
            [expectations :refer :all]
            [valtweet.core :refer :all]))

(let [store (-> #{}
                (post (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
                (post (->Tweet "Bob"   "Damn! We lost!"           (minus (now) (minutes 2))))
                (post (->Tweet "Bob"   "Good game though."        (minus (now) (minutes 1)))))]
  (expect (map :text (tweets-by store "Alice"))
          ["I love the weather today"])
  (expect (map :text (tweets-by store "Bob"))
          ["Good game though."
           "Damn! We lost!"])
  (expect (map :text (timeline-matching store (fn [tweet]
                                                (before? (minus (now) (minutes 3))
                                                         (:time tweet)))))
          ["Good game though."
           "Damn! We lost!"])


  (expect (timeline-matching store (fn [tweet]
                                     (before? (now)
                                              (:time tweet))))
          []))

(let [graph (-> {}
                (follow "Charlie" "Alice")
                (follow "Charlie" "Bob")
                (follow "Bob" "Charlie"))]
  (expect (follows? graph "Charlie" "Alice") true)
  (expect (follows? graph "Charlie" "Bob") true)
  (expect (follows? graph "Bob" "Charlie") true)
  (expect (follows? graph "Bob" "Alice") false)
  (expect (follows? graph "Alice" "Steve") false)
  (expect (follows? graph "Steve" "Jim") false))

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

  (expect (wall store graph "Alice")   [tweet-alice-1])
  (expect (wall store graph "Bob")     [tweet-bob-2
                                        tweet-alice-1
                                        tweet-bob-1])
  (expect (wall store graph "Charlie") [tweet-bob-2
                                        tweet-alice-1
                                        tweet-bob-1
                                        tweet-charlie-1])
  (expect (wall store graph "Steve")   []))
