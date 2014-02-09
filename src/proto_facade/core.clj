(ns proto-facade.core
  
  (import [com.google.protobuf Descriptors$Descriptor MessageOrBuilder Descriptors$FieldDescriptor]
          [java.util Map Map$Entry])
  )

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
  (if (instance? MessageOrBuilder obj)
    (convert-to-map obj)
    obj))

(defn convert-to-map 
  ([^MessageOrBuilder message]
    (convert-to-map (.getDescriptorForType message) message))
  ([^Descriptors$Descriptor descriptor ^MessageOrBuilder message]
    (let [key-set (into #{} (map (fn [^Descriptors$FieldDescriptor field] (.getName field)) (.getFields descriptor)))
          values (map (fn [^Descriptors$FieldDescriptor field] (resolve-value (.getField message field))) (.getFields descriptor))
          entry-set (into #{} (map (fn [^Descriptors$FieldDescriptor field] (entry-set (.getName field) (resolve-value (.getField message field)))) (.getFields descriptor)))]
    
		    (reify Map
		      
         (containsKey [this k]
		        (-> descriptor (.findFieldByName k) nil? not))
		      
		      (containsValue [this k] 
             (some #(= % k) values))
		      (entrySet [this] entry-set)
		      (get [this k]
		        (resolve-value (.getField message (.findFieldByName descriptor k))))
		      (hashCode [this]
		          (hash message))
		      (isEmpty [this] false)
		      (keySet [this] 
		        key-set)
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
		    
		    ))))
