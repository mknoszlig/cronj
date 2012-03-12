(ns cronj.periods
  (:import [java.util Calendar GregorianCalendar Date]
           [java.text SimpleDateFormat]))

(defprotocol Periodic
  (next-event [this seed]))

(defprotocol Displayable
  (display [this]))

(defn- clone-and-add [calendar unit num]
  (let [cc (.clone calendar)]
    (.add cc unit num)
    cc))

(defn- render [r marker]
  (str (:num r) marker))

(extend-type Date
  Displayable
  (display [this] (let [sdf (SimpleDateFormat. "HH:mm:ss z")]
                    (.format sdf this))))

(defrecord Seconds [num])
(extend-type Seconds
  Periodic
  (next-event [this seed]
              (clone-and-add seed Calendar/SECOND (:num this)))
  Displayable
  (display [this] (render this "s")))

(defrecord Minutes [num])
(extend-type Minutes
  Periodic
  (next-event [this seed]
              (clone-and-add seed Calendar/MINUTE  (:num this)))
  Displayable
  (display [this] (render this "m")))

(defrecord Hours [num])
(extend-type Hours
  Periodic
  (next-event [this seed] (clone-and-add seed Calendar/HOUR (:num this)))
  Displayable
  (display [this] (render this "h")))

(defrecord Days [num])
(extend-type Days
  Periodic
  (next-event [this seed] (clone-and-add seed Calendar/DAY_OF_YEAR (:num this)))
  Displayable
  (display [this] (render this "d")))

(defrecord DoW [week-days])
(extend-type DoW
  Periodic
  (next-event [this seed] nil)
  Displayable
  (display [this] (str (apply str (interpose ":" (:week-days this))) ":")))

(defrecord DoM [month-days])
(extend-type DoM
  Periodic
  (next-event [this seed] nil)
  Displayable
  (display [this] (str (apply str (interpose ":" (:month-days this))) ":")))
