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
