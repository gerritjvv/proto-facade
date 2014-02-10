# proto-facade

Takes a Message and wraps it in an object that will pose the message as a map.

The message is not parsed in any way, and behind the scenes the Descriptor and FieldDescriptors are used to get the values and keys of the object.

This implementation is simple but allows us to use proto messages as if they were simple maps (just as json data would be parsed in clojure), without
any extra cost of parsing the data into maps.

## Usage

As an example a Person.proto is provided on the ```test/resources/proto``` directory.

```clojure

(use 'proto-facade.core :reload)

(def p (-> (Data$Person/newBuilder) (.setName "hi")))

(def m (convert-to-map p))

(get m "name")
;; "hi"

```

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
