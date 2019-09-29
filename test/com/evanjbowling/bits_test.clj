(ns com.evanjbowling.bits-test
  (:require
   [clojure.string        :as string]
   [clojure.test          :refer [deftest is are]]
   [com.evanjbowling.bits :as b]))

(deftest test-char-bits
  (are [c expected] (= expected (b/char-bits c))
    "a"    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 1)
    "b"    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 1 0)
    "c"    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 1 1)
    "1"    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 1)
    "2"    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 1 0)
    "3"    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 1 1)
    "⁷"    '(0 0 1 0 0 0 0 0 0 1 1 1 0 1 1 1)))

(deftest test-short-bits
  (are [s expected] (= expected (b/short-bits s))
    Short/MAX_VALUE '(0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)
    Short/MIN_VALUE '(1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "0"             '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "255"           '(0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1)
    "-255"          '(1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1)))

(deftest test-int-bits
  (are [i expected] (= expected (b/int-bits i))
    Integer/MAX_VALUE '(0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)
    Integer/MIN_VALUE '(1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "0"               '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "255"             '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1)
    "-255"            '(1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1)))

(deftest test-long-bits
  (are [l expected] (= expected (b/long-bits l))
    Long/MAX_VALUE    '(0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                          1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)
    Long/MIN_VALUE    '(1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "0"               '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "255"             '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                          0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1)
    "-255"            '(1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                          1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1)))

(deftest test-float-bits
  (are [f expected]
       (->> (b/float-bits f)
            (map (partial string/join ""))
            ((partial = expected)))

    Float/POSITIVE_INFINITY ["0" "11111111" "00000000000000000000000"]
    Float/NEGATIVE_INFINITY ["1" "11111111" "00000000000000000000000"]
    Float/MAX_VALUE         ["0" "11111110" "11111111111111111111111"] ;; 3.4028235e38
    Float/MIN_NORMAL        ["0" "00000001" "00000000000000000000000"]
    Float/MIN_VALUE         ["0" "00000000" "00000000000000000000001"] ;; 1.4e-45
    Float/NaN               ["0" "11111111" "10000000000000000000000"]
    "0.0"                   ["0" "00000000" "00000000000000000000000"]
    "-0.0"                  ["1" "00000000" "00000000000000000000000"]
    "0.5"                   ["0" "01111110" "00000000000000000000000"]
    "0.25"                  ["0" "01111101" "00000000000000000000000"]
    "0.1"                   ["0" "01111011" "10011001100110011001101"]
    "9"                     ["0" "10000010" "00100000000000000000000"]))

(deftest test-float-attrs
  (is (= Float/MIN_EXPONENT (:exponent (b/float-attrs Float/MIN_VALUE))))
  (is (= Float/MAX_EXPONENT (:exponent (b/float-attrs Float/MAX_VALUE))))
  (are [f expected] (= expected (b/float-attrs f))
    Float/MAX_VALUE
    {:negative?       false
     :type            :normal
     :exponent-biased 254
     :exponent        127
     :fraction        16777215/8388608
     :rational        340282346638528859811704183484516925440N}

    Float/MIN_NORMAL
    {:negative?       false
     :type            :normal
     :exponent-biased 1
     :exponent        -126
     :fraction        1N
     :rational        1/85070591730234615865843651857942052864}

    Float/MIN_VALUE
    {:negative?       false
     :type            :subnormal
     :exponent-biased 0
     :exponent        -126
     :fraction        1/8388608
     :rational        1/713623846352979940529142984724747568191373312}

    Float/NaN
    {:type :nan}

    "0.0"
    {:negative?       false
     :type            :zero
     :exponent-biased 0
     :exponent        0
     :fraction        0
     :rational        0}

    "-0.0"
    {:negative?       true
     :type            :zero
     :exponent-biased 0
     :exponent        0
     :fraction        0
     :rational        0}

    Float/NEGATIVE_INFINITY
    {:negative? true
     :type      :infinity}

    Float/POSITIVE_INFINITY
    {:negative? false
     :type      :infinity}))

;; TODO: test-double-bits

