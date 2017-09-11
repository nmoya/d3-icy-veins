(ns d3-icy-veins.environment
  (:require [d3-icy-veins.utils :as utils]))

(defn get-env
  "Return environment configurations"
  []
  (let [env
        {:version (utils/get-project-version)
         :dump-date (new java.util.Date)}]
    (utils/save-all "resources/env.edn" "resources/env.json" "resources/env.min.json" env)))


