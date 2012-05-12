(ns cronj.cli
  (:use [cronj.core :only [Periodic Displayable Executable execute display]])
  (:import [java.util Date]))

(defmulti parser identity)

(defn get-period [str]
  (first (drop-while nil? (map #((parser %) str) (extenders Periodic)))))

(defn parse-period [spec]
  (when-let [[_ per-spec offset] (re-find #"^(.+)@(.+)$" spec)]
    [(get-period per-spec) ((parser Date) offset)]))


(defn add-job [id period-str cmd]
  (let [[period seed] (parse-period period-str)
        initial-time (atom nil)]
    (reify Executable
           (execute [this] (execute cmd))
           Displayable
           (display [this] (str id (display period))))))
  

(comment
  (add-job :daily-sales-stats     "1d@22:15:00 CET"          "rake statistics:daily_stats_mail RAILS_ENV=production")
  (add-job :product-cache-reload  "10m@00:00:00 UTC"         reload-cache-products)
  (add-job :secondly-shop-clicks  "10s@04:30:00 CET"         "rake gen-stats")
  (add-job :bimonthly-sales       "1:5:dom@04:30:00 CET"     "rake gen-sales-stats")
  (add-job :weekly-clicks         "mon:wed:dow@05:30:00 UTC" "rake weekly-clicks"))
