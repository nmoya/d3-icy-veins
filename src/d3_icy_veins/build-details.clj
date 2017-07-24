(ns d3-icy-veins.build-details
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [d3-icy-veins.utils :as utils]))

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
      (parse-active-skills (html/select layout [:span.d3_build_active_skill]))
     :passive
      (parse-passive-skills (html/select layout [:a.d3_build_passive_skill_link]))}
   :gear ()})

(defn get-build-details
  "Given a build structure, retrieve its details."
  [build]
  (let [url (:url build)
        layout (utils/fetch-url url)]
    (-> build
      (assoc :buildId (:id build) :credits (:url build))
      (dissoc :id)
      (merge
        {:id (str (gensym "build-description_"))}
        (parse-build-info layout)))))

(defn get-all-build-details
  "Opens builds.edn file and retrieve all build details"
  []
  (let [builds (read-string (slurp "./resources/builds.edn"))]
    (get-build-details (first (:demon-hunter builds)))))

(clojure.pprint/pprint
  (get-all-build-details))
