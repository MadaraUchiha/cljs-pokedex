(ns pokedex-cljs.core
  (:require [clojure.string :as str]
            [pokedex-cljs.helpers :as helpers]
            [pokedex-cljs.routing :as routing]
            [pokedex-cljs.state :as state]
            [reagent.dom :as d]))

;; -------------------------
;; Views

(defn home-page []
  (state/fetch-pokemon-list!)
  (fn []
    (let [{:keys [pokemon-list]} @state/store]
      [:div
       [:h1 "Pokédex"]
       [:div {:style {:display "grid" :grid-template-columns "repeat(5, 1fr)" :grid-gap "10px"}}
        (for [{:keys [url name]} pokemon-list]
          (let [id (helpers/extract-id-from-url url)]
            ^{:key id} [:div {:style {:text-align :center}}
                        [routing/link {:route :pokemon :params {:id id}}
                         [:img {:src (helpers/get-sprite-url id)}]
                         [:span {:style {:display :block}} (str/capitalize name)]]]))]])))

(defn pokemon-page []
  (let [id (get-in @state/store [:route :route-params :id])]
    (state/fetch-pokemon! id)
    (fn []
      [:div
       [routing/link {:route :home} "Back"]
       (case (state/get-api-status)
         :initial [:div "Loading..."]
         :loading [:div "Loading..."]
         :error   [:div "Error!"]
         :done    (let [{:keys [name types sprites]} (:pokemon @state/store)
                        {:keys [front_default]}      sprites]
                    [:div
                     [:h1 [:img {:src front_default}] (str (str/capitalize name) " #" id)]
                     [:p "Types: " (->> types
                                        (map (fn [type] ^{:key type} [:span (get-in type [:type :name])]))
                                        (interpose ", "))]]))])))


(defn app []
  (case (:handler (state/get-route))
    :pokemon [pokemon-page]
    [home-page]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (state/change-route! (routing/parse-route (.-pathname js/location)))
  (routing/init-routing!)
  ; (routing/setup-popstate-listener!)
  (d/render [app] (.getElementById js/document "app")))

(defn unmount-root []
  (routing/remove-popstate-listener!))

(defn ^:export init! []
  (mount-root))
