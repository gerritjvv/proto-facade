(ns proto-facade.core
  (:import [com.google.protobuf Descriptors$Descriptor MessageOrBuilder Descriptors$FieldDescriptor]
           [java.util Map Map$Entry]
           [com.google.protobuf ByteString]))

(defn entry-set [k v]
  (let [h (hash [k v])]
	  (reify Map$Entry
	    (getKey [this]
	      k)
	    (getValue [this]
	      v)
      (equals [this o]
        (and (instance? Map$Entry o)
             (= (.getKey ^Map$Entry o) k)
             (= (.getValue ^Map$Entry o) v)))
      (setValue [this v]
        (UnsupportedOperationException. "setValue not supported"))
	    (hashCode [this]
        h))))

(declare convert-to-map)

(defn resolve-value [obj]
  "Treat nested messages correctly, by calling convert-to-map and for lists (map resolve-value obj)"
  (cond
    (instance? MessageOrBuilder obj)
    (convert-to-map obj)
    (instance? java.util.Collection obj)
    (map resolve-value obj)
    (instance? ByteString obj)
    (if (nil? obj) 
      nil
      (.toByteArray ^ByteString obj))
    :else
    obj))

(defn convert-to-map 
  "Take a Message and wraps it in an object that will pose the message as a map."
  ([^MessageOrBuilder message]
    (convert-to-map (.getDescriptorForType message) message))
  ([^Descriptors$Descriptor descriptor ^MessageOrBuilder message]
    (let [key-set (map (fn [^Descriptors$FieldDescriptor field] (.getName field)) (.getFields descriptor))
          values (map (fn [^Descriptors$FieldDescriptor field] (resolve-value (.getField message field))) (.getFields descriptor))
          entry-set (map (fn [^Descriptors$FieldDescriptor field] (entry-set (.getName field) (resolve-value (.getField message field)))) (.getFields descriptor))]
    
		    (reify Map
        
         (containsKey [this k]
		        (-> descriptor (.findFieldByName k) nil? not))
		      
		      (containsValue [this k] 
             (true? (some #(= % k)
                       values)))
        
		      (entrySet [this] (set entry-set))
		      (get [this k]
		        (try 
                (resolve-value (.getField message (.findFieldByName descriptor k)))
                (catch NullPointerException npe nil)))
		      (hashCode [this]
		          (hash message))
		      (isEmpty [this] false)
		      (keySet [this] 
		        (set key-set))
		      (put [this k val]
		        (throw (UnsupportedOperationException. "put not supported")))
		      (putAll [this m]
		        (throw (UnsupportedOperationException. "put not supported")))
		      (remove [this k]
		        (throw (UnsupportedOperationException. "put not supported")))
		      (size [this]
		        (count key-set))
		      (values [this]
		        values)
		      (clear [this] 
		        (throw (UnsupportedOperationException. "put not supported")))
          (toString [this]
            ;better java interop
            (clojure.lang.RT/printString this))
		    
		    ))))
