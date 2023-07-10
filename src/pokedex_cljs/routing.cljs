(ns pokedex-cljs.routing
  (:require [bidi.bidi :as bidi]
            [pokedex-cljs.state :as state]))

(def routes ["/" {""               :home
                  ["pokemon/" :id] :pokemon}])

(defn parse-route [url]
  (bidi/match-route routes url))

(defn get-url [route & {:as params}]
  (bidi/path-for routes route params))

(defn navigate! [route & {:as params}]
  (let [target {:handler route :route-params params}]
    (state/change-route! target)
    (.pushState js/history (clj->js target) nil (get-url route params))))

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
  (setup-popstate-listener!)
  (-> js/location
    .-pathname
    parse-route
    clj->js
    (#(.replaceState js/history % nil))))