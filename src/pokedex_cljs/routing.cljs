(ns pokedex-cljs.routing
  (:require [bidi.bidi :as bidi]
            [pokedex-cljs.state :as state]
            [reagent.core :as r]))

(def routes ["/" {""               :home
                  ["pokemon/" :id] :pokemon}])

(defn parse-route [url]
  (bidi/match-route routes url))

(defn get-url [route & {:as params}]
  (bidi/path-for routes route params))

(defn navigate! [route & {:as params}]
  (state/change-route! {:handler route :route-params params}))

(defn link [{:keys [route params]} & children]
  (into [:a {:on-click #(navigate! route params) :style {:cursor "pointer"}}]
        children))

(defn handle-popstate [event]
  (let [state (-> event .-state js->clj)]
    (.info js/console "Popstate event" event state)
    (state/change-route! state)))

(defn setup-popstate-listener! []
  (.addEventListener js/window  "popstate" handle-popstate))

(defn remove-popstate-listener! []
  (.removeEventListener js/window  "popstate" handle-popstate))

(defn init-routing! []
  (r/track!
   #(let [{:keys [handler route-params]} @state/route
          current-url                    (.-pathname js/location)
          current-route-url              (get-url handler route-params)]
      (when-not (= current-url current-route-url)
        (.pushState js/history nil "" (get-url handler route-params)))))

  (setup-popstate-listener!)

  (-> js/location
      .-pathname
      parse-route
      clj->js
      (#(.replaceState js/history % nil))))
