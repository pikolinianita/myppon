(ns scores.reframe
  (:require [re-frame.core :as rf]
            [scores.data :as data]
            [scores.s-log :refer [log!]]))

(rf/reg-event-db 
 :init-db
 (fn [_ _]
   (log! :i "DB Init")
   {:screen :scoresheet
    :coeff data/coeff
    :jump-params data/starting-options
    :score data/sample-score
    :r-dict {:jpoints "Punkty Sedziowskie"
             :wind "Rekom. za wiatr"
             :distance "Punkty za dystans"
             :height "Punkty za wysokość"
             :total "Pkty W Sumie"}}))

(rf/reg-event-db
 :screen
 (fn [db [_ screen]]
   (assoc db :screen screen))) 

(rf/reg-sub
 :screen
 (fn [db]
   (db :screen)))

(rf/reg-event-db
 :try-submit
 (fn [db [_ inp]]
   (let [cf (inp :coeff)
         j-param (inp :params)]
     (if (data/data-ok? inp)
       (assoc db :coeff cf :jump-params j-param)
       db))))


(rf/reg-event-db
 :reroll
 (fn [db _]
   (let [params (db :jump-params)
         dist (data/score (params :distance))
         hei  (data/score (params :height))
         wind (data/score (params :wind))
         j-vals (take 5 (repeatedly #(data/score (params :judges))))]

     (-> db
         (assoc-in [:score :judges] j-vals)
         (assoc-in [:score :params :dist :value] dist)
         (assoc-in [:score :params :hei :value] hei)
         (assoc-in [:score :params :wind :value] wind)))))
 
(rf/reg-sub
 :judges-pure
 (fn [db]
   (get-in db [:score :judges]))) 
 
 
(rf/reg-sub
 :judges
 :<- [:judges-pure]
 (fn [pure-j]
   (data/strike-extrems pure-j)))


(rf/reg-sub
 :res-maps
 (fn [db]
   (get-in db [:score :params])))


(rf/reg-sub
 :jump-params
 (fn [db]
   (get-in db [:jump-params])))


(rf/reg-sub
 :coeff
 (fn [db]
   (get-in db [:coeff])))

(rf/reg-sub
 :r-dict
 (fn [db]
   (get-in db [:r-dict])))

(rf/reg-sub
 :results
 :<- [:res-maps]
 :<- [:coeff]
 :<- [:r-dict]
 :<- [:judges-pure]

 (fn [[res-map coeff dict judges] _]
   (let [d (* ((res-map :dist) :value) (:distance coeff))
         w (* ((res-map :wind) :value) (:wind coeff))
         h (* ((res-map :hei) :value) (:height coeff))
         jp (- (apply + judges) (apply min judges) (apply max judges))
         tot (+ d w jp h)]
     [[(:jpoints dict) jp]
      [(:wind dict) w]
      [(:distance dict) d]
      [(:height dict) h]
      [(:total dict) tot]])))












































