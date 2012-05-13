(ns cronj.periods
  (:use [cronj.core :only [Periodic Displayable defperiod]]
        [cronj.cli  :only [parser]])
  (:require [clojure.string :as s])
  (:import [java.util Calendar GregorianCalendar Date]
           [java.text SimpleDateFormat ParseException]))

(defn- clone-and-add [calendar unit num]
  (let [cc (.clone calendar)]
    (.add cc unit num)
    cc))

(defn- single-match-re [re]
  #(when-let [[_ r] (re-find re %)] (Integer/parseInt r)))

(defn- split-re [sep re]
  #(when-let [[_ r] (re-find re %)] (s/split r sep)))

(defn- render [r marker]
  (str (:num r) marker))

(extend-type Date
  Displayable
  (display [this] (let [sdf (SimpleDateFormat. "HH:mm:ss z")]
                    (.format sdf this))))
(defmethod parser Date [_]
           #(let [sdf (SimpleDateFormat. "HH:mm:ss z")]
              (try
                (.parse sdf %)
                (catch ParseException e nil))))


 (defperiod Seconds [num]
      (clone-and-add seed Calendar/SECOND (:num this))
      (render this "s")
      #(when-let [args ((single-match-re #"^([0-9]+)s$") %)]
         (Seconds. args)))


(defperiod Minutes [num]
  (clone-and-add seed Calendar/MINUTE  (:num this))
  (render this "m")
  #(when-let [args ((single-match-re #"^([0-9]+)m$") %)]
     (Minutes. args)))


(defperiod Hours [num]
  (clone-and-add seed Calendar/HOUR (:num this))
  (render this "h")
  #(when-let [args ((single-match-re #"^([0-9]+)h$") %)]
     (Hours. args)))


(defperiod Days [num]
  (clone-and-add seed Calendar/DAY_OF_YEAR (:num this))
  (render this "d")
  #(when-let [args ((single-match-re #"^([0-9]+)d$") %)]
     (Days. args)))


(defperiod DoW [week-days]
  nil
  (str (apply str (interpose ":" (:week-days this))) ":dow")
  #(when-let [args ((split-re #":" #"^(.+):dow$") %)]
     (DoW. args)))


(defperiod DoM [month-days]
  nil
  (str (apply str (interpose ":" (:month-days this))) ":")
  (fn [s]
    (when-let [args  (seq
                      ((split-re #":" #"^(.+):dom$") s))]
      (DoM. (map #(Integer/parseInt %) args)))))
