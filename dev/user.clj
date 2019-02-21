(ns user
  (:require [clojure.repl :refer :all]
            [clojure.tools.namespace.repl :as repl]
            [d3-icy-veins.builds :as builds]))

(repl/set-refresh-dirs "src" "dev")

(defn reset [] (repl/refresh))
(defn get-all-builds [] (builds/get-all))
(defn dump-all-builds [] (builds/get-all true))
(defn get-class-build [name] (builds/get-class-build name))
