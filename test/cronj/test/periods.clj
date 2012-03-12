(ns cronj.test.periods
  (:use [midje.sweet] [cronj.periods])
  (:import [java.util Date GregorianCalendar TimeZone]
           [cronj.periods Seconds Minutes Hours Days]))

(def tz (TimeZone/getTimeZone "CET"))
(def d (doto (GregorianCalendar. 2011 10 1 0 0 0)
         (.setTimeZone tz)))

(fact (.getTime (next-event (Seconds. 10) d)) =>
  (.getTime (doto (GregorianCalendar. 2011 10 1 0 0 10)
              (.setTimeZone tz))))


(fact (.getTime (next-event (Minutes. 10) d)) =>
  (.getTime (doto (GregorianCalendar. 2011 10 1 0 10 0)
              (.setTimeZone tz))))


(fact (.getTime (next-event (Hours. 24) d)) =>
  (.getTime (doto (GregorianCalendar. 2011 10 2 0 0 0)
              (.setTimeZone tz))))

(fact (.getTime (next-event (Days. 24) d)) =>
  (.getTime (doto (GregorianCalendar. 2011 10 25 0 0 0)
              (.setTimeZone tz))))
