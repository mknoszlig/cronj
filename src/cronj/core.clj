(ns cronj.core)


(defprotocol Periodic
  (next-event [this seed]))

(defprotocol Displayable
  (display [this]))

(defprotocol Executable
  (execute [this]))
