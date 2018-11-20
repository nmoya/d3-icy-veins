(defproject d3-icy-veins "0.1.0-SNAPSHOT"
  :description "Diablo 3 Icy Veins App"
  :url "https://github.com/nmoya/d3-icy-veins"
  :license {:name "MIT License"
            :url "https://en.wikipedia.org/wiki/MIT_License#License_terms"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [proto-repl "0.3.1"]
                 [camel-snake-kebab "0.4.0"]
                 [cheshire "5.7.1"]]
  :source-paths ["src" "dev"]
  :main ^:skip-aot user
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
