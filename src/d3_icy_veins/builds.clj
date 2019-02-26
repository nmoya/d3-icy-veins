(ns d3-icy-veins.builds
  (:require [camel-snake-kebab.core :as csk]
            [net.cgrand.enlive-html :as html]
            [d3-icy-veins.utils :as utils]
            [clojure.string :as string]))

(def ^:private blizz-base-img "https://blzmedia-a.akamaihd.net/d3/icons/items/large/")

(defn- create-class-builds-url
  "Given a class name, append builds, convert to kebab notation and append to base-url"
  [class-name]
  (str utils/base-url (csk/->kebab-case (str class-name " builds"))))

(defn- get-class-build-list
  "Layout will have all nav-bar entries. The current class entry is the last one."
  [layout]
  (first (take-last 2 (html/select layout [:div.nav_content_entries]))))

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

(defn- get-active-skill-image [idx layout]
  (let [img-fn (if (< idx 2) second first)]
    (img-fn (html/select layout [:img]))))

(defn- parse-active-skill [idx layout]
  {:id (utils/gen-id "active-skill_")
   :button (cond
              (= idx 0) "Left Mouse Button"
              (= idx 1) "Right Mouse Button"
              :else (utils/get-layout-content (html/select layout [:span.d3_build_active_skill_slot])))
   :skill {:image (utils/get-layout-src [(get-active-skill-image idx layout)])
           :name (utils/get-layout-content (html/select layout [:a.d3_active_skill]))}
   :rune {:image (utils/get-layout-src (html/select layout [:img.d3_icon]))
          :name (utils/get-layout-content (html/select layout [:span.d3_rune]))}})

(defn- parse-passive-skill [layout]
  {:id (utils/gen-id "passive-skill_")
   :name (utils/get-layout-content (html/select layout [:span.d3_build_passive_skill_name]))
   :image (utils/get-layout-src (html/select layout [:img]))})

(defn- parse-active-skills [layout]
  (map-indexed parse-active-skill (html/select layout [:span.d3_build_active_skill])))

(defn- parse-passive-skills [layout]
  (map parse-passive-skill (html/select layout [:a.d3_build_passive_skill_link])))

(defn- transform-gear-item [gear-item]
  (let [item (:item gear-item)
        iframe (:iframe gear-item)
        low-res (utils/get-layout-src [item])
        name (string/split low-res #"/")]
    {:name (utils/get-layout-alt [item])
     :image (str blizz-base-img (last name))
     :iframe iframe}))

(defn- parse-gear-gem-cube [layout id-prefix descriptions]
  (map-indexed (fn [idx gear]
                 (let []
                   {:description (nth descriptions idx)
                    :main (transform-gear-item (:main gear))
                    :alternatives (map transform-gear-item (:alternatives gear))}))
   layout))

(defn- d3-item-size-valid? [size]
  (or (= size 18)
      (= size 19)))

(defn- gear-names [size]
  (let [gear-names [:helmet :shoulders :gloves
                    :chest :belt :pants :boots
                    :bracers :amulet :ring :ring
                    :weapon]]
    (if (= size 18)
      gear-names
      (conj gear-names :offhand))))

(defn- gear-gem-cube? [content]
  (let [gear-gem-cube ["Helm: "
                       "Shoulders: "
                       "Gloves: "
                       "Chest: "
                       "Belt: "
                       "Pants: "
                       "Boots: "
                       "Bracers: "
                       "Amulet: "
                       "Ring 1: "
                       "Ring 2: "
                       "Weapon: "
                       "Weapon 1: "
                       "Weapon 2: "
                       "Offhand: "
                       "Legendary Gem 1: "
                       "Legendary Gem 2: "
                       "Legendary Gem 3: "
                       "Weapon Slot: "
                       "Armor Slot: "
                       "Jewelry Slot: "]]
    (some #(= content %) gear-gem-cube)))

(defn- transform-gear [gear-item-list]
  (let [items (html/select gear-item-list [:span :> :img.d3_icon.d3_item])
        iframes (html/select gear-item-list [:span.d3_icon_span :> :a])
        iframes-urls (map #(get-in % [:attrs :href]) iframes)
        items-iframes (map (fn [item iframe] {:item item :iframe iframe}) items iframes-urls)]
    {:main (first items-iframes)
     :alternatives (rest items-iframes)}))

(defn- build-gear-gem-cube-list [layout]
  (let [all-items (html/select layout [:ul :> :li])
        relevant-items (filter #(gear-gem-cube? (-> % :content first)) all-items)]
    (map transform-gear relevant-items)))

(defn- parse-build-info [layout]
  (let [title (utils/get-layout-content (html/select layout [:title]))
        gear-gem-cube (build-gear-gem-cube-list layout)
        last-gem (- (count gear-gem-cube) 3)
        first-gem (- last-gem 3)]
    (if-not (d3-item-size-valid? (count gear-gem-cube))
      (do
        (println (str "Failed: " title)))
        ;(throw (Exception. (str "Failed to parse gear / gem / cube for build: " title))))
      (let [gear (take first-gem gear-gem-cube)
            gem (-> gear-gem-cube vec (subvec first-gem last-gem))
            cube (take-last 3 gear-gem-cube)]
        {:skills
          {:active
            (take 6 (parse-active-skills layout))
           :passive
            (take 4 (parse-passive-skills layout))}
          :gear (parse-gear-gem-cube gear "gear_" (gear-names (count gear-gem-cube)))
          :gem (parse-gear-gem-cube gem "gem_"[:gem :gem :gem])
          :cube (parse-gear-gem-cube cube "cube_"[:cube-weapon :cube-armor :cube-jewelry])}))))

(defn- get-build-details
  "Given a build structure, retrieve its details."
  [build]
  (let [url (:url build)
        layout (utils/fetch-url url)]
    (merge build (parse-build-info layout))))

(defn- create-build-details
  "Creates a build structure from a layout"
  [d3-class layout]
  (println (:name d3-class))
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
  (map
    (fn [d3-class]
      {:name (:name d3-class) :builds (retrieve-builds-by-class d3-class)})
    utils/d3-classes))

(defn get-class-build
  [d3-class]
  (retrieve-builds-by-class {:name d3-class}))

(defn get-all
  "Public builds interface. Fetch the builds for all classes. Can save on disk."
  ([] (get-all false))
  ([save-to-file]
   (let [builds-map (retrieve-builds-all-classes)]
    (when save-to-file
      (utils/save-map-to-json "resources/builds.json" builds-map)
      (utils/save-map-to-json-minified "resources/builds.min.json" builds-map))
    builds-map)))

; (get-all true)
