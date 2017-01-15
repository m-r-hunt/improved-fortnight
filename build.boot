(set-env!
  :source-paths #{"src", "test"}

  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [adzerk/boot-test "1.1.2" :scope "test"]
                  [org.clojure/tools.namespace "0.2.11" :scope "test"]
                  [it.frbracch/boot-marginalia "0.1.3-1" :scope "test"]])

(require '[adzerk.boot-test :refer :all])

(require 'if.core)
(deftask run []
  (with-pass-thru _
    (if.core/-main)))

(require
 '[it.frbracch.boot-marginalia :refer [marginalia]])
