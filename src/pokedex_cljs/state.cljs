(ns pokedex-cljs.state
  (:require [reagent.core :as r]))

(defonce sample-pokemon {:id 1
                         :name "Bulbasaur"
                         :type "Grass"
                         :image "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
                         :description "A strange seed was planted on its back at birth. The plant sprouts and grows with this POKéMON."})

(defonce store (r/atom {:pokemon      {}
                        :pokemon-list []
                        :api-status   :not-started
                        :route        {:handler :home :route-params {}}}))

(defonce route (r/cursor store [:route]))

(defn fetch! [url]
  (-> (js/fetch url)
      (.then #(.json %))
      (.then #(js->clj % :keywordize-keys true))))

(defn fetch-pokemon-list! []
  (-> (fetch! "https://pokeapi.co/api/v2/pokemon?limit=151")
      (.then #(swap! store assoc :pokemon-list (:results %)))))

(defn fetch-pokemon! [id]
  (swap! store assoc :api-status :loading)
  (-> (fetch! (str "https://pokeapi.co/api/v2/pokemon/" id))
      (.then #(swap! store assoc :pokemon %))
      (.then #(swap! store assoc :api-status :done))
      (.catch #(swap! store assoc :api-status :error))))

(defn change-route! [{:keys [handler route-params]}]
  (.info js/console "Changing route to" (clj->js {:handler handler :route-params route-params}))
  (swap! store assoc :route {:handler handler :route-params route-params}))

(defn get-pokemon-list []
  (@store :pokemon-list))

(defn get-pokemon []
  (@store :pokemon))

(defn get-api-status []
  (@store :api-status))

(defn get-route []
  (@store :route))
