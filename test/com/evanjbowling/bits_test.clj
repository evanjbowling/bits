(ns com.evanjbowling.bits-test
  (:require
   [clojure.string        :as string]
   [clojure.test          :refer [deftest is are]]
   [com.evanjbowling.bits :as b]))

(deftest test-char-bits
  (are [c expected] (= expected (b/char-bits c))
    \a    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 1)
    \b    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 1 0)
    \c    '(0 0 0 0 0 0 0 0 0 1 1 0 0 0 1 1)
    \1    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 0 1)
    \2    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 1 0)
    \3    '(0 0 0 0 0 0 0 0 0 0 1 1 0 0 1 1)
    \⁷    '(0 0 1 0 0 0 0 0 0 1 1 1 0 1 1 1)))

(deftest test-short-bits
  (are [s expected] (= expected (b/short-bits s))
    Short/MAX_VALUE '(0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)
    Short/MIN_VALUE '(1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "0"             '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "255"           '(0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1)
    "-255"          '(1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1)))

(deftest test-from-short-bits
  (are [s] (= (Short/parseShort (str s)) (b/from-short-bits (b/short-bits s)))
    Short/MAX_VALUE
    Short/MIN_VALUE
    "0"))

(deftest test-int-bits
  (are [i expected] (= expected (b/int-bits i))
    Integer/MAX_VALUE '(0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1)
    Integer/MIN_VALUE '(1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "0"               '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
    "255"             '(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1)
    "-255"            '(1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 1)))

(deftest test-from-int-bits
  (are [i] (= (Integer/parseInt (str i)) (b/from-int-bits (b/int-bits i)))
    Integer/MAX_VALUE
    Integer/MIN_VALUE
    "0"))

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

(deftest test-from-long-bits
  (are [l] (= (Long/parseLong (str l)) (b/from-long-bits (b/long-bits l)))
    Long/MAX_VALUE
    Long/MIN_VALUE
    "0"))

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

(deftest test-from-float-bits
  (is (.isNaN ^Float (b/from-float-bits (b/float-bits Float/NaN))))
  (are [f] (= (Float/parseFloat (str f)) (b/from-float-bits (b/float-bits f)))
    Float/POSITIVE_INFINITY
    Float/NEGATIVE_INFINITY
    Float/MAX_VALUE
    Float/MIN_NORMAL
    Float/MIN_VALUE
    "0.0"
    "-0.0"
    "0.25"
    "0.1"))

(deftest test-double-bits
  (are [d expected]
       (->> (b/double-bits d)
            (map (partial string/join ""))
            ((partial = expected)))
    Double/POSITIVE_INFINITY ["0" "11111111111" "0000000000000000000000000000000000000000000000000000"]
    Double/NEGATIVE_INFINITY ["1" "11111111111" "0000000000000000000000000000000000000000000000000000"]
    Double/MAX_VALUE         ["0" "11111111110" "1111111111111111111111111111111111111111111111111111"]
    Double/MIN_NORMAL        ["0" "00000000001" "0000000000000000000000000000000000000000000000000000"]
    Double/MIN_VALUE         ["0" "00000000000" "0000000000000000000000000000000000000000000000000001"]
    "0.0"                    ["0" "00000000000" "0000000000000000000000000000000000000000000000000000"]
    "-0.0"                   ["1" "00000000000" "0000000000000000000000000000000000000000000000000000"]
    "9007199254740992"       ["0" "10000110100" "0000000000000000000000000000000000000000000000000000"]
    "9007199254740993"       ["0" "10000110100" "0000000000000000000000000000000000000000000000000000"]))

(deftest test-from-double-bits
  (is (.isNaN ^Double (b/from-double-bits (b/double-bits Double/NaN))))
  (are [d] (= (Double/parseDouble (str d)) (b/from-double-bits (b/double-bits d)))
    Double/POSITIVE_INFINITY
    Double/NEGATIVE_INFINITY
    Double/MAX_VALUE
    Double/MIN_NORMAL
    Double/MIN_VALUE
    "0.0"
    "-0.0"
    "0.25"
    "0.1"))

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

(deftest test-double-attrs
  (is (= Double/MIN_EXPONENT (:exponent (b/double-attrs Double/MIN_VALUE))))
  (is (= Double/MAX_EXPONENT (:exponent (b/double-attrs Double/MAX_VALUE))))
  (are [d expected] (= expected (b/double-attrs d))
    Double/MAX_VALUE
    {:negative?       false
     :type            :normal
     :exponent-biased 2046
     :exponent        1023
     :fraction        9007199254740991/4503599627370496
     :rational        179769313486231570814527423731704356798070567525844996598917476803157260780028538760589558632766878171540458953514382464234321326889464182768467546703537516986049910576551282076245490090389328944075868508455133942304583236903222948165808559332123348274797826204144723168738177180919299881250404026184124858368N}

    Double/MIN_NORMAL
    {:negative?       false
     :type            :normal
     :exponent-biased 1
     :exponent        -1022
     :fraction        1N
     :rational        1/44942328371557897693232629769725618340449424473557664318357520289433168951375240783177119330601884005280028469967848339414697442203604155623211857659868531094441973356216371319075554900311523529863270738021251442209537670585615720368478277635206809290837627671146574559986811484619929076208839082406056034304}

    Double/MIN_VALUE
    {:negative?       false
     :type            :subnormal
     :exponent-biased 0
     :exponent        -1022
     :fraction        1/4503599627370496
     :rational        1/202402253307310618352495346718917307049556649764142118356901358027430339567995346891960383701437124495187077864316811911389808737385793476867013399940738509921517424276566361364466907742093216341239767678472745068562007483424692698618103355649159556340810056512358769552333414615230502532186327508646006263307707741093494784}

    Double/NaN
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

    Double/NEGATIVE_INFINITY
    {:negative? true
     :type :infinity}

    Double/POSITIVE_INFINITY
    {:negative? false
     :type      :infinity}))

