(ns proto-facade.core-test
  (:require [proto-facade.core :refer :all])
  (:import [test Data$Person Data$Address])
  (:use [midje.sweet]))


(facts "Test convert to map function"
  
  (fact "Test simple string set get"
     
    (get (convert-to-map (-> (Data$Person/newBuilder) (.setName "hi"))) "name") => "hi")
  
  (fact "Test nested message set get"
    (let [m (convert-to-map 
			        (-> (Data$Person/newBuilder)
			            (.setAddress
				            (-> (Data$Address/newBuilder) 
				            (.setCity "ABC")
				            (.setCountry "EDF")))
			            (.build)))]
    
       (-> m (get "address") (get "city")) => "ABC"
       (-> m (get "address") (get "country")) => "EDF"))
  
  (fact "Test repeated nested message set get"
      (let [p-builder  (-> (Data$Person/newBuilder) )
            _ (dotimes [i 10]
			          (.addPrevAddresses p-builder
			            (-> (Data$Address/newBuilder)
			                (.setCity (str i))
			                .build)))
            m (convert-to-map p-builder)]
        
        (count (get m "prevAddresses")) => 10
        (loop [addresses (get m "prevAddresses") i 0]
          (if-let [address (first addresses)]
            (do 
              (get address "city") => (str i)
              (recur (rest addresses) (inc i))))))))
        
        
    
    
