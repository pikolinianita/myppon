(ns scores.s-log)

(defn log! [k & args]
	(if (keyword? k)
		(case k
		:i (.info js/console (apply str args))
		:e (.error js/console (apply str args))
		:d (.debug js/console (apply str args))
		:w (.warn js/console (apply str args))
		:l (.warn js/console (apply str args))
		:tr (.trace js/console (apply str args))
		 (.log js/console (apply str k args))
		)
		(.log js/console (apply str k args))
	)
)