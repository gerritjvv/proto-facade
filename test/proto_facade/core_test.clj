(ns proto-facade.core-test
  (:require [clojure.test :refer :all]
            [proto-facade.core :refer :all])
  (:import [test Data$Person]))

(comment
  
  (def p (-> (Data$Person/newBuilder) (.setName "hi")))
  )