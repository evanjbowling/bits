(defproject com.evanjbowling/bits "0.0.1-SNAPSHOT"
  :plugins      [[lein-cljfmt                 "0.6.4"]
                 [lein-cloverage              "1.0.13"]]
  :dependencies [[org.clojure/clojure         "1.8.0"]]
  :repl-options {:init-ns com.evanjbowling.bits}
  :global-vars  {*warn-on-reflection* true}
  :aliases {"lint" ["cljfmt"]}
  :profiles     
    {:uberjar {}})
