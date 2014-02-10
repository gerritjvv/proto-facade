(ns proto-facade.core
  (:import [com.google.protobuf Descriptors$Descriptor MessageOrBuilder Descriptors$FieldDescriptor]
           [java.util Map Map$Entry]
           [protofacade ProtoMap]
           [clojure.lang MapEntry]
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

(defn resolve-value [obj convert-f]
  "Treat nested messages correctly, by calling convert-to-map and for lists (map resolve-value obj)"
  (cond
    (instance? MessageOrBuilder obj)
    (convert-to-map obj {:convert-f convert-f})
    (instance? java.util.Collection obj)
    (map #(resolve-value % convert-f) obj)
    (instance? ByteString obj)
    (if (nil? obj) 
      nil
      (convert-f (.toByteArray ^ByteString obj)))
    :else
     (convert-f obj)))

(defn convert-to-map2 [message convert-f]
  (convert-to-map message {:convert-f convert-f}))

(defn convert-to-map 
  "Take a Message and wraps it in an object that will pose the message as a map."
  ([^MessageOrBuilder message]
    (convert-to-map (.getDescriptorForType message) message {} {}))
  ([^MessageOrBuilder message conf]
    (convert-to-map (.getDescriptorForType message) message {} conf))
  ([^Descriptors$Descriptor descriptor ^MessageOrBuilder message assoced-vals {:keys [convert-f] :or {convert-f identity} :as conf}]
    (let [
          get-f (fn [^Descriptors$FieldDescriptor field] 
                        (let [n (.getName field)]
                            (if-let [x (get assoced-vals n)] 
                              x 
                              (resolve-value (.getField message field) convert-f))))
          get-name (fn [v]
                     (if 
                       (instance? Descriptors$FieldDescriptor v)
                       (.getName ^Descriptors$FieldDescriptor v)
                       v))
          
          get-f2 (fn [k]
                   (if 
                     (instance? Descriptors$FieldDescriptor k)
                     (get-f k)
                     (get assoced-vals k)))
          
          mixed-keys (apply conj (keys assoced-vals) (.getFields descriptor))
          key-set (apply conj (keys assoced-vals) (map (fn [^Descriptors$FieldDescriptor field] (.getName field)) (.getFields descriptor)))
          values  (map (fn [field] (get-f2 field)) mixed-keys)
          entry-set (map (fn [field] (entry-set (get-name field) (get-f2 field))) mixed-keys)
          imap-entries (map (fn [field] (MapEntry. (get-name field) (get-f2 field))) mixed-keys)]
    
		    (reify ProtoMap
        
         (containsKey [this k]
		        (let [v (-> descriptor (.findFieldByName k) nil? not)]
              (if v v (not (nil? (get assoced-vals k))))))
                
		      
		      (containsValue [this k] 
             (true? (some #(= % k)
                       values)))
        
		      (entrySet [this] (set entry-set))
        
		      (get [this k]
		        (try ;if a field of the ProtoBuf get the value otherwise lookup in the assoced map
              (if (string? k)  
	              (if-let [field (.findFieldByName descriptor k)]
	                  (get-f field)
	                  (get-f2 k))
                (get-f2 k))
                (catch NullPointerException npe nil)))
		      (hashCode [this]
		          (hash [assoced-vals message]))
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
          
          (assoc [this k v]
            (convert-to-map descriptor message (assoc assoced-vals k v) conf))
          
          (entryAt [this k]
            (MapEntry. k (.get this k)))
            
          (valAt [this k]
            (.get this k))
          
          (count [this]
            (.size this))
          
          (cons [this v]
            (cons v imap-entries))
          
          (empty [this]
            this)
          (equiv [this obj]
            (= (hash obj) (.hashCode this)))
          
          (seq [this]
            imap-entries)))))
          
