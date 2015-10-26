(ns shl.service.chat
  (require [clj-http.client :as client]
           [shl.conf :as conf]))

(defn send-notification 
  ([channel-id color message] 
    (send-notification (:hipchat-api-key conf/conf) channel-id color message))
  ([api-key channel-id color message]
    (if (and (not (nil? api-key)) (not (nil? channel-id)))
	    (client/post 
	      (str "https://api.hipchat.com/v2/room/" channel-id "/notification?auth_token=" api-key)
	      {:form-params {:from "Solita Hockey League"
	                     :color color 
	                     :message message 
	                     :notify false 
	                     :message-format "html"}
	       :content-type :json
	       :socket-timeout 10000  ;; in milliseconds
         :conn-timeout 10000    ;; in milliseconds
         :accept :json
	      }))))