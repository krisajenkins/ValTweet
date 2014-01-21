(ns valtweet.ui-test
  (:require [clojure.test :refer :all]
            [valtweet.core :refer :all]
            [valtweet.ui :refer :all]
            [clj-time.core :refer [minus now minutes before?]]))

(defn wrap-setup
  [test-function]
  (let [fixed-now (clj-time.core/now)]
    (with-redefs [now (constantly fixed-now)]
      (test-function))))

(use-fixtures :once wrap-setup)

(deftest format-tweet-test
  (is (= (format-tweet (->Tweet "Alice" "I love the weather today" (minus (now) (minutes 5))))
         "Alice - I love the weather today (5 minutes ago)")))

(deftest parse-command-test
  (are [command-string result] (= (parse-command command-string)
                                  result)

       "Alice -> Nice day." {:command :post
                             :username "Alice"
                             :text "Nice day."}
       "Bob -> Says you."   {:command :post
                             :username "Bob"
                             :text "Says you."}

       "Alice"              {:command :read
                             :username "Alice"}
       "Bob"                {:command :read
                             :username "Bob"}

       "Alice follows Bob"  {:command :follow
                             :username "Alice"
                             :follows-username "Bob"}
       "Bob follows Steve"  {:command :follow
                             :username "Bob"
                             :follows-username "Steve"}

       "Alice wall"         {:command :wall
                             :username "Alice"}

       "Bob wall"           {:command :wall
                             :username "Bob"}))
