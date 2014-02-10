(ns proto-facade.core-test
  (:require [proto-facade.core :refer :all])
  (:import [test Data$Person Data$Address]
           [protofacade Converter]
           [com.google.protobuf ByteString])
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
  
  (fact "Test bytes field"
    (let [bts (byte-array 10)
          m (convert-to-map 
			        (-> (Data$Person/newBuilder)
			            (.setImg (ByteString/copyFrom bts))
			            (.build)))]
      (count (get m "img")) => (count bts)))
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
              (recur (rest addresses) (inc i)))))))
    
   
   (fact "Test Assoc"
     (let [m (convert-to-map 
				        (-> (Data$Person/newBuilder)
                    (.setName "nn")))
           m2 (assoc m "name" "aa")
           m3 (assoc m :abc 1)]
       
       (get m "name") => "nn"
       (get m2 "name") => "aa"
       (get m3 :abc) => 1
       
       ))
   (fact "Test map interface values"
      (let [m (convert-to-map 
				        (-> (Data$Person/newBuilder)
                    (.setName "nn")
				            (.setAddress
					            (-> (Data$Address/newBuilder) 
					            (.setCity "ABC")
					            (.setCountry "EDF")))
				            (.build)))]
        
        (.keySet m) => #{"address" "email" "id" "img" "likes" "name" "prevAddresses"}
        ;(.values m) => [0 "nn" "" [] {"country" "EDF", "postCode" "", "city" "ABC"} []]
        (.containsValue m "nn") => true
        (.containsValue m "zzz") => false
        (.containsKey m "address") => true
        (.containsKey m "bla") => false
        (.size m) => (count (.keySet m))
        
        (let [entries (.entrySet m)]
          (count entries) => (count (.keySet m))
          (sort (map #(.getKey %) entries)) => ["address" "email" "id" "img" "likes" "name" "prevAddresses"])))
      (fact "Test java Converter class"
        
        (let [m (Converter/convertToMap
                   (-> (Data$Person/newBuilder)
						            (.setAddress
							            (-> (Data$Address/newBuilder) 
							            (.setCity "ABC")
							            (.setCountry "EDF")))
						            (.build)))]
          (-> m (get "address") (get "city")) => "ABC"
          (-> m (get "address") (get "country")) => "EDF"))
       (fact "Test java Converter class with converter function"
        
        (let [m (Converter/convertToMap
                   (-> (Data$Person/newBuilder)
						            (.setAddress
							            (-> (Data$Address/newBuilder) 
							            (.setCity "ABC")
							            (.setCountry "EDF")))
						            (.build))
                   #(string? %))]
          (-> m (get "address") (get "city")) => true
          (-> m (get "address") (get "country")) => true)))
          
        
        
        
    
    
