(ns cronj.executors
  (:use [cronj.core]))


(extend clojure.lang.IFn
  Executable
  {:execute (fn [this] (this))})

(defrecord Shell [cmd]
  Executable
  (execute [this]
           (.exec (Runtime/getRuntime) cmd)))

(extend java.lang.String
  Executable
  {:execute (fn [this] (execute (Shell. this)))})
