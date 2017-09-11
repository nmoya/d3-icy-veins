(ns d3-icy-veins.builds
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [d3-icy-veins.utils :as utils]))

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

(defn- parse-active-skill [idx layout]
  {:id (utils/gen-id "active-skill_")
   :button (cond
              (= idx 0) "Left Mouse Button"
              (= idx 1) "Right Mouse Button"
              :else (utils/get-layout-content (html/select layout [:span.d3_build_active_skill_slot])))
   :skill {:image (utils/get-layout-src [(first (html/select layout [:img]))])
           :name (utils/get-layout-content (html/select layout [:a.d3_active_skill]))}
   :rune {:image (utils/get-layout-src (html/select layout [:img.d3_icon]))
          :name (utils/get-layout-content (html/select layout [:span.d3_rune]))}})

(defn- parse-passive-skill [layout]
  {:id (utils/gen-id "passive-skill_")
   :name (utils/get-layout-content (html/select layout [:span.d3_build_passive_skill_name]))
   :image (utils/get-layout-src (html/select layout [:img]))})

(defn- parse-active-skills [layout]
  (map-indexed parse-active-skill layout))

(defn- parse-passive-skills [layout]
  (map parse-passive-skill layout))

(defn- parse-build-info [layout]
  {:skills
    {:active
      (take 6 (parse-active-skills (html/select layout [:span.d3_build_active_skill])))
     :passive
      (take 4 (parse-passive-skills (html/select layout [:a.d3_build_passive_skill_link])))}
   :gear ()})

(defn- get-build-details
  "Given a build structure, retrieve its details."
  [build]
  (let [url (:url build)
        layout (utils/fetch-url url)]
    (merge build (parse-build-info layout))))

(defn- create-build-details
  "Creates a build structure from a layout"
  [d3-class layout]
  (get-build-details
    {:id (utils/gen-id "build_")
     :name (get-build-name (html/select layout [:a]))
     :url (get-build-url (html/select layout [:a]))
     :d3-class-name (:name d3-class)
     :tier (get-build-tier (html/select layout [:span.nav_content_block_d3_build_tier]))}))

(defn- retrieve-builds-by-class
  "From a class name, build the url, fetch the url and process the build list."
  [d3-class]
  (let [url (create-class-builds-url (:name d3-class))
        layout (utils/fetch-url url)
        builds (get-class-build-list layout)]
    (map
      (partial create-build-details d3-class)
      (html/select builds [:div.nav_content_block_d3_build]))))

(defn- retrieve-builds-all-classes
  "Iterates over utils/classes to parse every class' build."
  []
  (reduce-kv
    (fn [acc key d3-class]
      (assoc acc key (retrieve-builds-by-class d3-class)))
    ; {} {:demon-hunter {:name "Demon Hunter"}}))
    {} utils/d3-classes))

(defn get-all
  "Public builds interface. Fetch the builds for all classes. Can save on disk."
  ([] (get-all false))
  ([save-to-file]
   (let [builds-map (retrieve-builds-all-classes)]
    (when save-to-file
      (utils/save-map-to-edn "resources/builds.edn" builds-map)
      (utils/save-map-to-json "resources/builds.json" builds-map)
      (utils/save-map-to-json-minified "resources/builds.min.json" builds-map))
    builds-map)))

; (get-all true)
