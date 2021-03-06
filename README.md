# proto-facade

Takes a Message and wraps it in an object that will pose the message as a map.

The message is not parsed in any way, and behind the scenes the Descriptor and FieldDescriptors are used to get the values and keys of the object.

This implementation is simple but allows us to use proto messages as if they were simple maps (just as json data would be parsed in clojure), without
any extra cost of parsing the data into maps.


## Dependency

[![Clojars Project](http://clojars.org/proto-facade/latest-version.svg)](http://clojars.org/proto-facade)

## Usage

As an example a Person.proto is provided on the ```test/resources/proto``` directory.

Note: before you can run the example you need:

* have protoc installed 
* run: ```lein protobuf``` to build the proto classes
 
 
### From Clojure

```clojure

(use 'proto-facade.core :reload)
(import 'test.Data$Person)

(def p (-> (Data$Person/newBuilder) (.setName "hi")))
(def m (convert-to-map p))

(get m "name")
;; "hi"

(keys m)
;; ("email" "id" "likes" "address" "prevAddresses" "name")

(vals m)
;; ("" 0 () {"country" "", "postCode" "", "city" ""} () "hi")

(count m)
;; 6

;;assoc to override a value
(def m2 (assoc m "name" 1))

(get m2 "name")
;; 1

;;assoc to add a new key
(def m2 (assoc m :abc "a"))

(get m2 :abc)
;; "a"

;;add a function to convert values

```


## From java

```java

import java.util.Map;
import test.Data;
import protofacade.Converter;

Data.Person p = Data.Person.newBuilder().setName("test").build();
Map<String, Object> m = Converter.convertToMap(p);
System.out.println(m.get("name"));
// "test"		

```

For a detailed usage see: https://github.com/gerritjvv/proto-facade/blob/master/test/proto_facade/core_test.clj

# Byte Arrays

All byte arrays are converted from the ```ByteString``` class to a byte array.

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
