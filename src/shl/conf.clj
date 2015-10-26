(ns shl.conf)

(def conf {
	:shl-api-port    (Integer/parseInt (or (System/getenv "SHL_API_PORT")    "8080"))
	:hipchat-api-key                   (or (System/getenv "SHL_HIPCHAT_API_KEY") nil)})
