(ns com.evanjbowling.bits.impl
  (:refer-clojure :exclude [zero? type]))

(def class-specs
  {Character
   {:bits 16
    :bit-groups [{:start 0, :length 16, :name nil, :format :twos-ncomplement}]}
   Short
   {:bits 16
    :bit-groups [{:start 0, :length 16, :name nil, :format :twos-ncomplement}]}
   Integer
   {:bits 32
    :bit-groups [{:start 0, :length 32, :name nil, :format :twos-complement}]}
   Long
   {:bits 64
    :bit-groups [{:start 0, :length 64, :name nil, :format :twos-complement}]}
   Float
   {:bits 32
    :exponent-bias -127
    :bit-groups [{:start 0, :length 1, :name "sign", :format :unsigned}
                 {:start 1, :length 8, :name "exponent", :format :unsigned}
                 {:start 9, :length 23, :name "fraction", :format :unsigned}]}
   Double
   {:bits 64
    :exponent-bias -1023
    :bit-groups [{:start 0, :length 1, :name "sign", :format :unsigned}
                 {:start 1, :length 11, :name "exponent", :format :unsigned}
                 {:start 12, :length 52, :name "fraction", :format :unsigned}]}})

(defn ^:private get-bits
  "Returns sequence of Long 1/0 values corresponding to
  big-endian encoding of x in n bits."
  [n x]
  (->> (range n)
       (map (fn [i] (bit-and x (bit-shift-left 0x1 i))))
       (map (fn [i] (if (= 0 i) 0 1)))
       (reverse)))

(defn ^:private primitive-bits
  "Returns bit-sequence generating fn for wrapped
  primitive class c, by using integer-producing fn
  ifn (bit shift operations can only be applied to
  integer values) and optionally parsing a string
  with string-parsing fn sfn. Returned fn throws
  msg if the argument is not of the correct type."
  [c ifn sfn ^String msg]
  (fn [x]
    (let [bfn (->> (get class-specs c)
                   :bits
                   (partial get-bits))]
      (cond
        (= c (class x)) (-> x ifn bfn)
        (= String (class x)) (-> x sfn ifn bfn)
        :else (throw (IllegalArgumentException. msg))))))

(def char-bits
  (primitive-bits
   Character
   short
   (fn [^String s]
     (when-not (= 1 (count s))
       (throw (IllegalArgumentException. "string does not contain exactly one char")))
     (.charAt s 0))
   "not a character"))

(def short-bits
  (primitive-bits
   Short
   identity
   #(Short/parseShort %)
   "not a short"))

(def int-bits
  (primitive-bits
   Integer
   identity
   #(Integer/parseInt %)
   "not an integer"))

(def long-bits
  (primitive-bits
   Long
   identity
   #(Long/parseLong %)
   "not a long"))

(defn ^:private grouped-primitive-bits
  "Returns fn to produce bit string for primitive
  class that can be represented by ordered group
  of bits."
  [c ifn sfn msg]
  (comp
   (fn [bs]
     (->> (get class-specs c)
          :bit-groups
          (sort-by :start)
          (map #(subvec (vec bs) (:start %) (+ (:start %) (:length %))))))
   (primitive-bits c ifn sfn msg)))

(def float-bits
  (grouped-primitive-bits
   Float
   #(Float/floatToRawIntBits %)
   #(Float/parseFloat %)
   "not a float"))

(defn from-bits
  "Converts big-endian sequence of bits into integer
  value."
  [bs]
  (let [num-bits (count (flatten bs))]
    (loop [bits (reverse (flatten bs))
           i    1
           acc  0]
      (if (empty? bits)
        acc
        (let [acc' (if (= 0 (first bits))
                     acc
                     (bit-or acc i))]
          (recur
           (rest bits)
           (bit-shift-left i 1)
           acc'))))))

(defn from-char-bits
  [bs]
  (char (from-bits bs)))

(defn from-short-bits
  [bs]
  (short (from-bits bs)))

(defn from-int-bits
  [bs]
  (int (from-bits bs)))

(defn from-long-bits
  [bs]
  (float (from-bits bs)))

(defn from-float-bits
  [bs]
  (Float/intBitsToFloat (from-bits bs)))

(defn from-double-bits
  [bs]
  (Double/longBitsToDouble (from-bits bs)))

(def double-bits
  (grouped-primitive-bits
   Double
   #(Double/doubleToRawLongBits %)
   #(Double/parseDouble %)
   "not a double"))

(def class->bits-fn
  {Character char-bits
   Short     short-bits
   Integer   int-bits
   Long      long-bits
   Float     float-bits
   Double    double-bits})

(defn ^:private abs
  [x]
  (cond-> x (neg? x) (* -1N)))

(defn ^:private pow
  [base e]
  (if (= e 0)
    1
    (cond->> (apply *' (repeat (abs e) base))
      (neg? e) (/ 1))))

(defn ^:private sum-digits
  "Returns sum of the digit sequence assuming
  big-endian order.

  Examples:
    (sum-digit-seq 2 2 [1 1 0])
    ;;=> 6"
  [base exponent-start digits]
  (->> digits
       (map-indexed (fn [i d] (*' d (pow base (-' exponent-start i)))))
       (apply +')))

(defn ^:private zeros?
  [coll]
  (every? (partial = 0) coll))

(defn ^:private ones?
  [coll]
  (every? (partial = 1) coll))

(defn ^:private zero?
  "Checks if the floating point bit sequence is a
  zero value."
  [[s e f]]
  (and (zeros? e) (zeros? f)))

(defn ^:private subnormal?
  "Checks if the floating point bit sequence is a
  subnormal value (i.e. significand has an implicit
  leading 0 rather than 1)."
  [[s e f]]
  (and (zeros? e) (not (zeros? f))))

(defn ^:private infinity?
  "Checks if the floating point bit sequence is an
  infinite value."
  [[s e f]]
  (and (ones? e) (zeros? f)))

(defn ^:private nan?
  "Checks if the floating point bit sequence is a
  nan value."
  [[s e f]]
  (and (ones? e) (not (zeros? f))))

(defn ^:private type
  "Returns the type of a floating point bit
  sequence."
  [bits]
  (cond
    (infinity? bits)  :infinity
    (nan? bits)       :nan
    (zero? bits)      :zero
    (subnormal? bits) :subnormal
    :else             :normal))

(defn ^:private attr-fn
  [c]
  (fn [f]
    (let [bits-fn (get class->bits-fn c)
          exponent-bias (:exponent-bias (get class-specs c))
          [[s] e f :as bits] (bits-fn f)
          float-type (type bits)
          fraction-full (cons
                         (if (= float-type :subnormal)
                           0
                           1)
                         f)]
      (cond-> {:negative? (= 1 s)
               :type float-type}
        (nan? bits) (dissoc :negative?)

        (#{:subnormal :normal} float-type)
        (#(let [exp-biased (sum-digits 2 (-> e count dec) e)
                exp        (+' exp-biased exponent-bias)
                fraction   (sum-digits 2 0 fraction-full)]
            (merge
             %
             {:exponent-biased exp-biased
              :exponent exp
              :fraction fraction
              :rational (*' fraction (pow 2 exp))})))))))

(def float-attrs (attr-fn Float))
(def double-attrs (attr-fn Double))

