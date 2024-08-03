(defproject com.evanjbowling/bits "0.0.1"
  :plugins      [[lein-cljfmt                 "0.6.4"]
                 [lein-cloverage              "1.0.13"]]
  :dependencies [[org.clojure/clojure         "1.8.0"]]
  :global-vars  {*warn-on-reflection* true}
  :aliases      {"lint" ["cljfmt"]}
  :repl-options {:init-ns user.examples}
  :profiles     
    {:dev {:source-paths ["src" "dev"]}
     :uberjar {}})
