(ns cronj.cli
  (:require [cronj.periods :as cp]
            [clojure.string :as s]
            [cronj.executors :as ex])
  (:import [cronj.periods Seconds Minutes Hours Days DoM DoW]
           [cronj.executors Executable]
           [java.util Date GregorianCalendar]
           [java.text SimpleDateFormat ParseException]))

(defn- single-match-re [re]
  #(when-let [[_ r] (re-find re %)] (Integer/parseInt r)))

(defn- split-re [sep re]
  #(when-let [[_ r] (re-find re %)] (s/split r sep)))


(defmulti parser identity)
(defmethod parser Seconds [_] #(when-let [args ((single-match-re #"^([0-9]+)s$") %)]
                                 (Seconds. args)))
(defmethod parser Minutes [_] #(when-let [args ((single-match-re #"^([0-9]+)m$") %)]
                                 (Minutes. args)))
(defmethod parser Hours   [_] #(when-let [args ((single-match-re #"^([0-9]+)h$") %)]
                                 (Hours. args)))
(defmethod parser Days    [_] #(when-let [args ((single-match-re #"^([0-9]+)d$") %)]
                                 (Days. args)))
(defmethod parser DoM     [_] (fn [s]
                                (when-let [args  (seq
                                                  ((split-re #":" #"^(.+):dom$") s))]
                                  (DoM. (map #(Integer/parseInt %) args)))))
(defmethod parser DoW     [_] #(when-let [args ((split-re #":" #"^(.+):dow$") %)]
                                 (DoW. args)))
(defmethod parser Date    [_] #(let [sdf (SimpleDateFormat. "HH:mm:ss z")]
                                 (try
                                   (.parse sdf %)
                                   (catch ParseException e nil))))
(defn get-period [str]
  (first (drop-while nil? (map #((parser %) str) (extenders cp/Periodic)))))

(defn parse-period [spec]
  (when-let [[_ per-spec offset] (re-find #"^(.+)@(.+)$" spec)]
    [(get-period per-spec) ((parser Date) offset)]))


(defn add-job [id period-str cmd]
  (let [[period seed] (parse-period period-str)
        initial-time (atom nil)]
    (reify ex/Executable
           (execute [this] (ex/execute cmd))
           cp/Displayable
           (display [this] (str id (cp/display period))))))
  

(comment
  (add-job :daily-sales-stats     "1d@22:15:00 CET"          "rake statistics:daily_stats_mail RAILS_ENV=production")
  (add-job :product-cache-reload  "10m@00:00:00 UTC"         reload-cache-products)
  (add-job :secondly-shop-clicks  "10s@04:30:00 CET"         "rake gen-stats")
  (add-job :bimonthly-sales       "1:5:dom@04:30:00 CET"     "rake gen-sales-stats")
  (add-job :weekly-clicks         "mon:wed:dow@05:30:00 UTC" "rake weekly-clicks"))
