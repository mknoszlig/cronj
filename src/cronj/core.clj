(ns cronj.core
  (:import [java.util.concurrent PriorityBlockingQueue]
           [java.lang Comparable]))

(defprotocol Periodic
  (next-event [this seed]))

(defprotocol Displayable
  (display [this]))
