(ns d3-icy-veins.utils
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [cheshire.core :as json]))
(use '[clojure.pprint :only (pprint)])

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

(defn get-layout-alt
  "Variable layout was deconstructed from a list of a single <a> element."
  [[layout]]
  (-> layout :attrs :alt))

(defn gen-id
  "Generate an unique ID for a structure"
  [prefix]
  (str (gensym prefix)))

(defn get-project-version
  "Reads project.clj and retrieves the current version"
  []
  (-> "./project.clj" slurp read-string (nth 2)))

(def d3-classes [{:name "Demon Hunter"}
                 {:name "Barbarian"}
                 {:name "Witch Doctor"}
                 {:name "Crusader"}
                 {:name "Monk"}
                 {:name "Wizard"}
                 {:name "Necromancer"}])

(defn save-map-to-edn [filepath content]
  "Receives a file name and a map, prints the map into a string and saves the string in filepath"
  (println (str "Saved " filepath))
  (spit filepath (with-out-str (clojure.pprint/pprint content))))

(defn save-map-to-json [filepath content]
  "Receives a file name and a map, prints the map into a string and saves the string in filepath"
  (println (str "Saved " filepath))
  (spit filepath (json/generate-string content {:pretty true})))

(defn save-map-to-json-minified [filepath content]
  "Receives a file name and a map, prints the map into a string and saves the string in filepath"
  (println (str "Saved " filepath))
  (spit filepath (json/generate-string content)))

(defn save-all
  "Saves content in edn, json, and minified json"
  [edn-path json-path json-min-path content]
  (save-map-to-edn edn-path content)
  (save-map-to-json json-path content)
  (save-map-to-json-minified json-min-path content))
