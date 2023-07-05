(ns pokedex-cljs.helpers
  (:require [clojure.string :as string]))

(defn extract-id-from-url [pokeapi-url]
  (->> (string/split pokeapi-url #"/")
       (last)))

(defn get-sprite-url [id]
  (str "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" id ".png"))