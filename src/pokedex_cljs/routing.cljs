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
  (state/change-route! {:handler route :route-params params})
  (.pushState js/history nil "" (get-url route params)))

(defn link [{:keys [route params]} & children]
  (into [:a {:on-click #(navigate! route params) :style {:cursor "pointer"}}]
        children))

(defn setup-popstate-listener! []
  (.addEventListener
   js/window
   "popstate"
   (fn []
     (-> js/location
         .-pathname
         parse-route
         state/change-route!))))