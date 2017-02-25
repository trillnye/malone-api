(ns malone.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema IteneraryItem
  {:title s/Str
   :description s/Str
   :distance s/Num
   :event_time s/Num
   :location {:address s/Str
              :lat s/Num
              :long s/Num}})

(s/defschema IteneraryRequest
  {:start_time s/Num
   :end_time s/Num
   :start_point s/Str})

(defn get-itenerary [start-time end-time start-point]
  (println (str "Received " start-time end-time start-point))
  [{:title "First title"
                  :description "First description"
                  :event_time 1487983960
                  :distance 1.0
                  :location {:address "First address"
                             :lat 1111.11111
                             :long -1111.11111}}
   {:title "Second title"
                  :description "Second description"
                  :event_time 1487983960
                  :distance 2.0
                  :location {:address "Second address"
                             :lat 2222.22222
                             :long -2222.22222}}
   {:title "Third title"
                  :description "Third description"
                  :event_time 1487983960
                  :distance 3.0
                  :location {:address "Third address"
                             :lat 3333.33333
                             :long -3333.33333}}])

(def app
  (api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "Malone"
                    :description "Malone API"}
             :tags [{:name "api", :description "Malone APIs"}]}}}

    (context "/api" []
      :tags ["api"]

      (POST "/itenerary" []
        :return [IteneraryItem]
        :body [itenerary_request IteneraryRequest]
        :summary "Generates an itenerary for a given start/end time and initial location"
        (ok (get-itenerary 1 1 "test"))))))
