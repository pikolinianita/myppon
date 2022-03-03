(ns scores.catcher 
(:require  [scores.s-log :refer [log!]]
[reagent.core :as reagent]
))


(defn catcher [text component]
(log! :i "inside catcher: " text)
(reagent/create-class {
 :display-name "Catcher!"
 :component-did-mount #(log! :l "catcher! " text " mounted!" )
 :component-did-update #(log! :l "catcher! " text " updated!" )
 :get-derived-state-from-error (fn [err] (log! :w "inside error stuff") [:div "catcher! " " Failed" ])
 :component-did-catch #(log! :l "catcher! " text " Failed" % )
 :reagent-render (fn [text component]
	(log! :i "inside render")
	[:div text [component]] 
 )
}

))

(defn thrower []
(log! :i "inside thrower: ")
(throw (js/Error. "Fucked"))
[:div "thrower"]
)

(defn not-thrower []
	(log! :i "inside not-thrower: ")
	[:div "Not Thrower"]
)