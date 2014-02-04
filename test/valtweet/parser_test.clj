(ns valtweet.parser-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [valtweet.parser :refer :all]))

(facts parse-command-test
  (tabular
      (fact "Parsing commands"
        (parse-command ?command-string) => ?result)

      ?command-string      ?result

      ""                   {:command :noop}

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
