(ns scores.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]
     [scores.core :refer [multiply]]
	 [scores.data :as data]
	 [malli.core :as m] 
	 [malli.error :as me]))

(deftest multiply-test
  (is (= (* 1 2) (multiply 1 2))))

(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))

(deftest strike-function
	(testing "simple Function"
		(is (= [[1 true] [2 false] [3 false] [4 false] [5 true]] (data/strike-extrems [1 2 3 4 5]))))
	(testing "known bug"	
		(is (= [[1 false] [1 false] [1 false] [1 false] [1 true]] (data/strike-extrems [1 1 1 1 1])))
		))
	
(def skeme [:and :double [:fn #(not (js/isNaN %))]])
	
(deftest schemas 
	(testing "coeff"
		(is (m/validate data/coeff-validation data/coeff))
	    (is (= nil (me/humanize (m/explain data/coeff-validation data/coeff) )))
	)
	(testing "pos"
		(is (m/validate data/options-validation data/starting-options))
		(is (= nil (me/humanize (m/explain data/options-validation data/starting-options) )))
	)
	
	
	
	(testing "NAN"
	(is (m/validate skeme (js/parseFloat "zser")))
	(is (= nil (me/humanize (m/explain skeme (js/parseFloat "zser")))))
	(is (m/validate skeme (js/parseFloat "123")))
	(is (= nil (me/humanize (m/explain skeme (js/parseFloat "123")))))
	(is (= nil (js/isNaN(js/parseFloat "zser"))))
	
	)
)