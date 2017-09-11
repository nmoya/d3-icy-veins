(ns d3-icy-veins.core
  (:require [d3-icy-veins.builds :as builds])
  (:gen-class))

(defn main
  "Entry point of the dumper"
  [& args]
  (print "Arguments: ")
  (println args)
  (builds/get-all-builds true))


; (main)