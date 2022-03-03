(ns scores.data
(:require  [scores.s-log :refer [log!]]
			[malli.core :as m]
[malli.error :as me]			))

(def countries {:pl {:name "Polska" :flag "pic/pl.png"}
				:aut {:name "Austria" :flag "pic/at.png"}
				:de {:name "Niemcy" :flag "pic/de.png"}
				:jp {:name "Japonia" :flag "pic/jp.png"}
				:cze {:name "Czechy" :flag "pic/cz.png"}
})

;{:name "" :flag "pic/"}

(def country-vec [:pl :aut :de :jp :cze])

(defn score 
	([mmm]
	(score (:min mmm) (:max mmm))
	)
    ([min max] 
	(+ min (rand (- max min))))
)

(def sample-score 
{
:judges [10.1 11.2 12.3 13.4 14.5]  
:params {
	:dist {:name "Odległość" :value 120 :suffix "m"}
	:hei {:name "Wysokość" :value 31 :suffix "m"}
	:wind {:name "Wiatr" :value 1.7 :suffix "m/s"}
}
})

(def starting-options {

:distance {:min 90 :max 150}
:height {:min 1 :max 10}
:judges {:min 10 :max 20}
:wind {:min -2 :max 2}

})


(def coeff {:jpoints 1 :wind 20 :distance 1.2 :height 1.5})

(defn strike-extrems [v]
;(log! :e v)
(let [min-idx (first (apply min-key second (map-indexed vector v)))
      max-idx (first (apply max-key second (map-indexed vector v)))
	  strike-extr (fn [[pos val]]
								(if (or (= pos min-idx) (= pos max-idx)) [val true] [val false] ))
]
	(mapv strike-extr (map-indexed vector v))
	;[min-idx max-idx]
))

(def pos-d [:and :double [:> 0]]);

(def pos-min-max [:and [:map [:min pos-d]
							 [:max pos-d]]
					   [:fn {:error/message "Min not smaller than max"} (fn [{:keys [min max]}] (> max min))]])

(def coeff-validation 
[:map  {:closed true} 
	[:jpoints pos-d]
	[:wind [:and :double [:fn {:error/message "NaN"} #(not (js/isNaN %))]]]
	[:distance pos-d]
	[:height pos-d]
])

(def options-validation
[:map
	[:distance pos-min-max ]
	[:height pos-min-max]
	[:judges pos-min-max]
	[:wind  [:and [:map [:min :double]
							 [:max :double]]
					   [:fn {:error/message "Min not smaller than max"} (fn [{:keys [min max]}] (> max min))]]]

]
)

(defn data-ok? [{:keys [coeff params]}] 
	;(log! :i "dat fail" coeff " " params)
	;(log! :e "result " (m/validate coeff-validation coeff))
	(and (m/validate coeff-validation coeff) (m/validate options-validation params))
)

(defn whats-wrong [{:keys [coeff params]}]
	;(log! :w  (m/explain coeff-validation coeff))
	;(log! :w (m/explain options-validation params))
	(str (me/humanize (m/explain coeff-validation coeff)) (me/humanize (m/explain options-validation params)))
)








