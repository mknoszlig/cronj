(ns cronj.periods
  (:use [cronj.core :only [Periodic Displayable]]
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


(defrecord Seconds [num])
(extend-type Seconds
  Periodic
  (next-event [this seed]
              (clone-and-add seed Calendar/SECOND (:num this)))
  Displayable
  (display [this] (render this "s")))
(defmethod parser Seconds [_]
           #(when-let [args ((single-match-re #"^([0-9]+)s$") %)]
              (Seconds. args)))


(defrecord Minutes [num])
(extend-type Minutes
  Periodic
  (next-event [this seed]
              (clone-and-add seed Calendar/MINUTE  (:num this)))
  Displayable
  (display [this] (render this "m")))
(defmethod parser Minutes [_]
           #(when-let [args ((single-match-re #"^([0-9]+)m$") %)]
              (Minutes. args)))


(defrecord Hours [num])
(extend-type Hours
  Periodic
  (next-event [this seed] (clone-and-add seed Calendar/HOUR (:num this)))
  Displayable
  (display [this] (render this "h")))
(defmethod parser Hours [_]
           #(when-let [args ((single-match-re #"^([0-9]+)h$") %)]
              (Hours. args)))


(defrecord Days [num])
(extend-type Days
  Periodic
  (next-event [this seed] (clone-and-add seed Calendar/DAY_OF_YEAR (:num this)))
  Displayable
  (display [this] (render this "d")))
(defmethod parser Days [_]
           #(when-let [args ((single-match-re #"^([0-9]+)d$") %)]
              (Days. args)))


(defrecord DoW [week-days])
(extend-type DoW
  Periodic
  (next-event [this seed] nil)
  Displayable
  (display [this] (str (apply str (interpose ":" (:week-days this))) ":")))
(defmethod parser DoW [_]
           #(when-let [args ((split-re #":" #"^(.+):dow$") %)]
              (DoW. args)))


(defrecord DoM [month-days])
(extend-type DoM
  Periodic
  (next-event [this seed] nil)
  Displayable
  (display [this] (str (apply str (interpose ":" (:month-days this))) ":")))
(defmethod parser DoM [_]
           (fn [s]
             (when-let [args  (seq
                               ((split-re #":" #"^(.+):dom$") s))]
               (DoM. (map #(Integer/parseInt %) args)))))
