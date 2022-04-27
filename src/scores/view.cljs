(ns scores.view
  (:require [cljs.pprint :refer [cl-format]]
            [scores.data :as data]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
			 [cljs.pprint :refer [pprint cl-format]]
		  
		   [re-frame.db :as rfdb]))

(defn card [c-code [score strike?]]
  [:div.column.is-one-fifth {:key c-code}
   [:div.card
    [:div.card-content.has-text-centered
     [:figure.image.is-inline-block
      [:img {:src (get-in data/countries [c-code :flag])}]]
     [:div.content.has-text-centered (when strike? {:style {:text-decoration "line-through"}}) (cl-format nil "~$" score)]]]])

(defn refree-cards []
  (let [countries data/country-vec
        scores @(rf/subscribe [:judges])]
    [:div.columns
     (map #(card (countries %) (scores %)) (range 5))]))

(defn reload [text mod]
  [:button.button.is-danger {:on-click (fn [_] (rf/dispatch [:reroll mod]))} text])

(defn screen-btn [text destination]
  [:button.button {:on-click (fn [_] (rf/dispatch [:screen destination]))} text])

(defn submit-data-btn [text inp]
  [:button.button.is-primary {:on-click (fn [_] (rf/dispatch [:try-submit @inp]))} text])

(defn buttons []
  [:div.buttons
   [screen-btn "Scores" :scoresheet]
   [screen-btn "Input" :input]
  ; [screen-btn "devcards" :devcards]
   [reload "Super!!!" :max]
   [reload "OK!!!" :avg]
   [reload "Faul!!!" :min]])

(defn details []
  (let [maps (vals @(rf/subscribe [:res-maps]))]
    [:div.columns.is-multiline
     (for [m maps]
       [:div.column {:key (:name m)}
        [:div.card (:name m) " " (cl-format nil "~$" (:value m)) " " (:suffix m) "  "]])]))

(defn results []
  (let [vectrs @(rf/subscribe [:results])]
    [:div.columns.is-multiline
     (for [[name value] vectrs]
       (do
         [:div.column {:key name}
          [:div.card.has-text-centered name " " (cl-format nil "~$" value) " "]]))]))

(defn input-coeff [inp text k-word]
  [:<>
   text
   [:input {:type :number
            :value ((@inp :coeff) k-word)
            :name k-word
            :on-change (fn [e]
                         (swap! inp assoc-in [:coeff k-word]  (js/parseFloat (-> e .-target .-value))))}]])

(defn params-min-max [inp text k-word]
  [:div.p-1.is-size-6 text
   [:div.mx-1 "min" [:input.mx-2 {:type :number
                        :value (((@inp :params) k-word) :min)
                        :name k-word
                        :on-change (fn [e]
                                     (swap! inp assoc-in [:params k-word :min]  (js/parseFloat (-> e .-target .-value))))}]]
   [:div.mx-1 "max" [:input.mx-2 {:type :number
                        :value (((@inp :params) k-word) :max)
                        :name k-word
                        :on-change (fn [e]
                                     (swap! inp assoc-in [:params k-word :max]  (js/parseFloat (-> e .-target .-value))))}]]])

(defn dev-cards []
  [:div "cards"
   [:div "Rand Score: " (str (take 10 (repeatedly #(data/score 10 20))))]
   [:div "country card" [card :pl [17.4 false]]]
   [:div "country striked card" [card :pl [1.4 true]]]
   [:div "refree cards" [refree-cards]]
   [:div "vals " [details]]
   [:div "results" [results]]
   [:div "reload: " [reload "re-roll"]]
   [:div "buttons" [buttons]]])

(defn input-data []
  (let [inp (reagent/atom {:coeff @(rf/subscribe [:coeff]) :params @(rf/subscribe [:jump-params])})]
    (fn []
      [:div.p-2
       [:div.p-2.is-size-4 "Will Display here important data: " #_(str @inp)]
       [:div.p-2 "Przeliczniki"
        [input-coeff inp " Wiatru " :wind]
        [input-coeff inp " Sedziowski " :jpoints]
        [input-coeff inp " Dystansu " :distance]
        [input-coeff inp " Wysokości " :height]]
       [:div.p-2.is-size-5 "Zakres wyników"
        [params-min-max inp " Wiatr: " :wind]
        [params-min-max inp " Oceny Sędziów: " :judges]
        [params-min-max inp " Dystans: " :distance]
        [params-min-max inp " Wysokość: " :height]]
       (if
        (data/data-ok? @inp)
         [:div.has-text-primary "ok!"]
         [:div.has-text-danger (data/whats-wrong (@inp :coeff))])
       [submit-data-btn "Zapisz Zmiany" inp]
       [buttons]])))

(defn display-scores []
  [:div
   [:div.is-size-4.has-text-centered "MYPPON"]
   [details]
   [refree-cards]
   [results]
   [buttons]
	;[:button {:on-click (fn [e] (rf/dispatch [:screen :devcards]))} "show devcards"]
	;[reload "Skok!"]
   ])

(defn selector []
  [:div
   (case @(rf/subscribe [:screen])
     :devcards [dev-cards]
     :scoresheet [display-scores]
     :input [input-data]
     [display-scores])
	[:pre (with-out-str (pprint @rfdb/app-db))]
   ])