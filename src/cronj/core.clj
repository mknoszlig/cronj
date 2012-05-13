(ns cronj.core)


(defprotocol Periodic
  (next-event [this seed]))

(defprotocol Displayable
  (display [this]))

(defprotocol Executable
  (execute [this]))

(defmacro defperiod
  "conveniently extend a (potentially existing) type to the Periodic and Displayable protocols and providing a cli.parser function - sets up the wiring for use in cronj.
Usage:
  (defperiod type-to-extend next-event-form display-form parser-fn) ; for existing types
, eg existing java classes
  (defperiod record-to-create arg-vector next-event-form display-form parser-fn) ; for a new type

 the parameters next-event-form, display-form and parser-fn are used directly as the implementations of the corresponding protocol functions and multimethod dispatch respectively. next-event-form has two vars that can be used for the implementation: this and seed.display-form provides this. parser-fn expects a function that receives a string as input and, if applicable, returns an instance of the record based on the string contents, nil otherwise."
  ([x-type next-event display parse-fn]
     `(do
        (extend-type ~x-type
          Periodic
          (~'next-event [~'this ~'seed] ~next-event)
          Displayable
          (~'display [~'this] ~display))
        (defmethod ~'parser ~x-type [~'_] ~parse-fn)))
  ([c-type args next-event display parse-fn]
     `(do
       (defrecord ~c-type ~args)
       (defperiod ~c-type ~next-event ~display ~parse-fn))))
