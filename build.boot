(set-env!
  :source-paths #{"src", "test"}

  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [adzerk/boot-test "1.1.2" :scope "test"]
                  [org.clojure/tools.namespace "0.2.11"]])

(require '[adzerk.boot-test :refer :all])

(require 'if.core)
(deftask run []
  (with-pass-thru _
    (if.core/-main)))
