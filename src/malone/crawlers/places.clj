(ns malone.crawlers.places
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))


(def config {:default-radius 4828
             :default-limit 10
             :api-key (env :google-places-key)
             :api-uri-root "https://maps.googleapis.com/maps/api/place"})

(def radar-search-route "/radarsearch/json?location=%s,%s&radius=%s&type=%s&keyword=%s&key=%s")
(def place-detail-route "/details/json?placeid=%s&key=%s")
(def nearby-search-route "/nearbysearch/json?location=%s,%s&type=%s&keyword=%s&key=%s&rankby=distance")

(defn from-unix-time
  "Return a Java Date object from a Unix time representation expressed
  in whole seconds."
  [unix-time]
  (c/from-date (java.util.Date. unix-time)))

(defn get-place-detail [place-id]
  (let [endpoint (format (str (:api-uri-root config) place-detail-route) place-id (:api-key config))
        result (:result (parse-string (:body (client/get endpoint)) true))]
    {:name (:name result)
     :address (:formatted_address result)
     :geo (:geometry result)}))

(defn list-radar-places [lat long keyword]
  (let [endpoint (format (str (:api-uri-root config) radar-search-route) lat long (:default-radius config) keyword keyword (:api-key config))
        results (take (:default-limit config) (:results (parse-string (:body (client/get endpoint)) true)))]
    (map #(get-place-detail (:place_id %)) results)))


(defn list-nearby-places [lat long keyword]
  (let [endpoint (format (str (:api-uri-root config) nearby-search-route) lat long keyword keyword (:api-key config))
        results (take (:default-limit config) (:results (parse-string (:body (client/get endpoint)) true)))]
    (map #(get-place-detail (:place_id %)) results)))
