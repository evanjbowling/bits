(ns user.examples
  (:require
    [com.evanjbowling.bits :as bits]
    [clojure.string :as str]
    [clojure.pprint :as pp]))

(defn print-shorts [shorts]
  (let [max-s (apply max (map (comp count str) shorts))]
    (doseq [s shorts]
      (let [padded-val (pp/cl-format nil (str "~" max-s "@a") s)
            ->str (fn[x](str/join "" x))]
        (printf "%s %s\n"
          padded-val
          (->str (bits/short-bits (short s))))))))

(defn print-all-the-shorts []
  (print-shorts (map short (range Short/MIN_VALUE Short/MAX_VALUE))))

