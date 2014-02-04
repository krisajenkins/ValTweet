(ns valtweet.parser-test
  (:require [expectations :refer :all]
            [valtweet.parser :refer :all]))

(given [command-string result] (expect (parse-command command-string) result)
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
                        :username "Bob"})
