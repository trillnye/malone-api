(ns malone.crawlers.wordpress
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))


(def get-posts-route "/wp-json/wp/v2/posts")

(defn from-unix-time
  "Return a Java Date object from a Unix time representation expressed
  in whole seconds."
  [unix-time]
  (c/from-date (java.util.Date. unix-time)))

(defn get-posts [site]
  (let [endpoint (str site  get-posts-route)]
   (map (fn [post]
          {:title (:title post)
           ;:body (:body post)
           :date (from-unix-time (* 1000 (:timestamp post)))})
    (:posts (:response (parse-string (:body (client/get endpoint)) true))))))

(defn filter-post [day-of-year year posts]
  (filter #((and (= year (.getYear (:date %)))
                  = day-of-year (.getDayOfYear (:date %)))) posts))
