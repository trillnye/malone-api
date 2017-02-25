(ns malone.crawlers.maps
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [environ.core :refer [env]]
            [clojure.string :as s]))

(def config {:api-geocoding-key (or (env :google-geocoding-key) "")
             :api-directions-key (or (env :google-maps-api-key) "")
             :api-uri-root "https://maps.googleapis.com/maps/api"})

(def directions-route "/directions/json?origin=%s&destination=%s&key=%s")
(def geocode-route "/geocode/json?address=%s&key=%s")

(defn url-encode [value]
  (s/join "+" (s/split value #" ")))

(defn get-directions [origin destination]
  (let [{uri-root :api-uri-root
         api-key :api-directions-key} config
        endpoint (format (str uri-root directions-route) (url-encode origin) (url-encode destination) api-key)]
    (parse-string (:body (client/get endpoint)) true)))

(defn get-geocoded-location [address]
  (let [{uri-root :api-uri-root
         api-key :api-geocoding-key} config
        endpoint (format (str uri-root geocode-route) (url-encode address) api-key)
        results (:results (parse-string (:body (client/get endpoint)) true))
        {lat :lat
         long :lng } (:location (:geometry (first results)))]
    {:lat lat
     :long long}))
