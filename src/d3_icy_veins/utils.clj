(ns d3-icy-veins.utils
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [cheshire.core :as json]))

(def base-url "https://www.icy-veins.com/d3/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn get-layout-content
  "Variable layout was deconstructed from a list of a single element."
  [[layout]]
  (html/text layout))

(defn get-layout-url
  "Variable layout was deconstructed from a list of a single <a> element."
  [[layout]]
  (str "https:" (get-in layout [:attrs :href])))

(defn get-layout-src
  "Variable layout was deconstructed from a list of a single <a> element."
  [[layout]]
  (str "https:" (get-in layout [:attrs :src])))

(defn gen-id
  "Generate an unique ID for a structure"
  [prefix]
  (str (gensym prefix)))

(def classes {:demon-hunter {:name "Demon Hunter"}
              :barbarian {:name "Barbarian"}
              :witch-doctor {:name "Witch Doctor"}
              :crusader {:name "Crusader"}
              :monk {:name "Monk"}
              :wizard {:name "Wizard"}
              :necromancer {:name "Necromancer"}})

(defn save-map-to-edn [filepath content]
  "Receives a file name and a map, prints the map into a string and saves the string in filepath"
  (clojure.pprint/pprint (str "Saved " filepath))
  (spit filepath (with-out-str (clojure.pprint/pprint content))))

(defn save-map-to-json [filepath content]
  "Receives a file name and a map, prints the map into a string and saves the string in filepath"
  (clojure.pprint/pprint (str "Saved " filepath))
  (spit filepath (json/generate-string content)))
