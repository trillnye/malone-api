(ns malone.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
            [malone.crawlers.places :as places]
            [malone.crawlers.maps :as gmaps]))

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

(defn place-to-itinerary-item [place]
  (let [{lat :lat
         long :long
         title :name
         description :name
         address :address} place]
    {:title title
     :description description
     :event_time 1487983960
     :distance 1.0
     :location {:address address
                :lat lat
                :long long}}))

(defn get-initial-waypoint [start-point]
  (let [{origin-lat :lat
         origin-long :long} (gmaps/get-geocoded-location start-point)
         marta-station (first (places/list-nearby-places origin-lat origin-long "marta station"))]
    marta-station))

(defn get-itinerary-item [origin-place activity]
  (println "Searching for " activity " near " (:title origin-place))
  (let [{place-lat :lat
         place-long :long} (:location origin-place)
        events (places/list-nearby-places place-lat place-long activity)]
    (println "Found " (count events) " events")
    (place-to-itinerary-item (rand-nth events))))

(defn get-itinerary [start-point activities]
  (let [starting-marta-station (get-initial-waypoint start-point)]
    (reduce (fn [acc item]
              (println "acc: " acc "(" (count acc) ")" "| item: " item)
              (conj acc (get-itinerary-item (last acc) item))) [(place-to-itinerary-item starting-marta-station)] activities)))


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
