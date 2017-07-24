(ns d3-icy-veins.builds
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [d3-icy-veins.utils :as utils]
            [cheshire.core :as json]))

(defn- create-class-builds-url
  "Given a class name, append builds, convert to kebab notation and append to base-url"
  [class-name]
  (str utils/base-url (csk/->kebab-case (str class-name " builds"))))

(defn- get-class-build-list
  "Layout will have all nav-bar entries. The current class entry is the last one."
  [layout]
  (last (html/select layout [:div.nav_content_entries])))

(defn- get-build-name
  "Given the right <a> tag, get its content."
  [layout]
  (utils/get-layout-content layout))

(defn- get-build-tier
  "Given the right span tag, get its content."
  [layout]
  (utils/get-layout-content layout))

(defn- get-build-url
  "Given the right <a> tag, get its url."
  [layout]
  (utils/get-layout-url layout))

(defn- create-build-details
  "Creates a build structure from a layout"
  [layout]
  {:name (get-build-name (html/select layout [:a]))
   :url (get-build-url (html/select layout [:a]))
   :tier (get-build-tier (html/select layout [:span.nav_content_block_d3_build_tier]))})

(defn- parse-class-builds
  "From a class name, build the url, fetch the url and process the build list."
  [class]
  (let [url (create-class-builds-url (:name class))
        layout (utils/fetch-url url)
        builds (get-class-build-list layout)]
    (map
      create-build-details
      (html/select builds [:div.nav_content_block_d3_build]))))

(defn- parse-all-builds
  "Iterates over utils/classes to parse every class' build."
  []
  (reduce-kv
    (fn [acc key value]
      (assoc acc key (parse-class-builds value)))
    {} utils/classes))

(defn get-all-builds
  "Public builds interface. Fetch the builds for all classes. Can save on disk."
  ([] (get-all-builds false))
  ([save-to-file]
   (let [builds-map (parse-all-builds)]
    (when save-to-file
      (utils/save-map-to-edn "resources/builds.edn" builds-map)
      (utils/save-map-to-json "resources/builds.json" builds-map))
    builds-map)))

(get-all-builds)
