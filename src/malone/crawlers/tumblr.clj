(ns malone.crawlers.tumblr
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [environ.core :refer [env]]))


(def config {:api-key (env :tumblr-api-key)
             :api-uri-root "https://api.tumblr.com"})

(def get-posts-route "/v2/blog/%s/posts/text?api_key=%s&filter=text")

(defn from-unix-time
  "Return a Java Date object from a Unix time representation expressed
  in whole seconds."
  [unix-time]
  (c/from-date (java.util.Date. unix-time)))

(defn get-posts [site]
  (let [endpoint (format (str (:api-uri-root config) get-posts-route) site (:api-key config))]
   (map (fn [post]
          {:title (:title post)
           ;:body (:body post)
           :date (from-unix-time (* 1000 (:timestamp post)))})
    (:posts (:response (parse-string (:body (client/get endpoint)) true))))))

(defn filter-post [day-of-year year posts]
  (filter #((and (= year (.getYear (:date %)))
                  = day-of-year (.getDayOfYear (:date %)))) posts))
