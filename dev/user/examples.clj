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

(defn zeros?
  [coll]
  (every? (partial = 0) coll))

(defn ones?
  [coll]
  (every? (partial = 1) coll))

(defn increment-bits
  [coll]
  {:pre [(not (ones? coll))]}
  (let [n (count coll)
        s (str/join "" coll)
        inc-long (inc (Long/parseLong s 2))
        inc-bits (bits/long-bits inc-long)]
    (->> (reverse inc-bits)
         (take n)
         reverse)))

(defn decrement-bits
  [coll]
  {:pre [(not (zeros? coll))]}
  (let [n (count coll)
        s (str/join "" coll)
        dec-long (dec (Long/parseLong s 2))
        dec-bits (bits/long-bits dec-long)]
    (->> (reverse dec-bits)
         (take n)
         reverse)))

(defn pos-next-float
  [[s e f]]
  {:pre [(= 0 (first s))]}
  (if (not (ones? f))
    (bits/from-float-bits [s e (increment-bits f)])
    (let [max-exponent? (and (ones? (butlast e)) (= 0 (last e)))]
      (if (not max-exponent?)
        (bits/from-float-bits [s (increment-bits e) (repeat 23 0)])
        (bits/from-float-bits [s (repeat 8 1) (repeat 23 0)])))))

(defn neg-next-float
  [[s e f]]
  {:pre [(= 1 (first s))]}
  (if (not (zeros? f))
    (bits/from-float-bits [s e (decrement-bits f)])
    (if (not (zeros? e))
      (bits/from-float-bits [s (decrement-bits e) (repeat 23 1)])
      (bits/from-float-bits [0 (repeat 8 0) (repeat 23 0)]))))

(defn next-float
  [f]
  (let [[[s] e f :as fbits] (bits/float-bits f)
        nan?   (and (ones? e) (not (zeros? f)))
        positive? (= 0 s)
        negative-infinity? (and (not positive?) (ones? e) (zeros? f))
        positive-infinity? (and positive? (ones? e) (zeros? f))]
    (cond
      nan? nil
      positive-infinity? nil
      negative-infinity? (* -1 Float/MAX_VALUE)
      positive? (pos-next-float fbits)
      :else (neg-next-float fbits))))

(defn float-seq [initial-float n]
  (loop [f initial-float
         i 0
         coll []]
    (if (= n i)
       coll
       (recur (next-float f) (inc i) (conj coll f)))))

(defn float-seq [initial-float n]
  (lazy-seq
    (when (< 0 n)
      (cons initial-float (float-seq (next-float initial-float) (dec n))))))

