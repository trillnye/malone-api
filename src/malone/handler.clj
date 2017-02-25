(ns malone.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]))

(s/defschema ItineraryItem
  {:title s/Str
   :description s/Str
   :distance s/Num
   :event_time s/Num
   :location {:address s/Str
              :lat s/Num
              :long s/Num}})

(s/defschema ItineraryRequest
  {:activities [s/Str]
   :start_point s/Str})

(defn get-itinerary [start-point activities]
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


(defroutes app-routes
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Malone"
                   :description "Malone API"}
            :tags [{:name "api", :description "Malone APIs"}]}}}

   (context "/api" []
            :tags ["api"]
            (POST "/itinerary" []
                  :return [ItineraryItem]
                  :body [itinerary_request ItineraryRequest]
                  :summary "Generates an itinerary for a given start/end time and initial location"
                  (ok (get-itinerary (:start_point itinerary_request) (:activities itinerary_request)))))))

(def app (-> app-routes
             (wrap-cors :access-control-allow-origin #".+"
                        :access-control-allow-methods [:get :put :post :delete])
             (wrap-defaults api-defaults)))
