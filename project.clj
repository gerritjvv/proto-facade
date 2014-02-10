(defproject proto-facade "0.3.2"
  :description "Utility that exposes a ProtoBuff Message as a Map"
  :url "https://github.com/gerritjvv/proto-facade"
  :license {:name "Apache v2"
            :url "https://github.com/gerritjvv/proto-facade/blob/master/LICENSE"}
  

  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :java-source-paths ["java"]
  :warn-on-reflection true
  :global-vars {*warn-on-reflection* true
                *assert* false}
  
  :dependencies [[com.google.protobuf/protobuf-java "2.5.0" :scope "provided"]
		 [midje "1.6-alpha2" :scope "test"]
                 [org.clojure/clojure "1.5.1"]]
  
  :proto-path "test/resources/proto"
  :plugins [
         [lein-protobuf "0.1.1"]
         [lein-midje "3.0.1"] [lein-marginalia "0.7.1"] 
         [lein-kibit "0.0.8"] [no-man-is-an-island/lein-eclipse "2.0.0"]
           ])
