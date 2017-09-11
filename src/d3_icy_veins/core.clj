(ns d3-icy-veins.core
  (:require [d3-icy-veins.builds :as builds]
            [d3-icy-veins.environment :as env])
  (:gen-class))

(defn main
  "Entry point of the dumper"
  [& args]
  (print "Arguments: ")
  (println args)
  (env/get-env)
  (builds/get-all true))

; (main)
