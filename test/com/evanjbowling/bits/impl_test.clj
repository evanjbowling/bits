(ns com.evanjbowling.bits.impl-test
  (:require
   [clojure.test               :refer [deftest is are]]
   [com.evanjbowling.bits.impl :as i]))

(deftest test-abs
  (are [x expected]
       (= expected (#'i/abs x))
    4  4
    -4 4))

(deftest test-pow
  (are [b e expected]
       (= expected (#'i/pow b e))
    2 -2 1/4
    2 -1 1/2
    2  0 1
    2  1 2
    2  2 4))

(deftest test-sum-exponent-bits
  (are [digits expected]
       (= expected (#'i/sum-exponent-bits digits))
    [0 0 0 1 1] 3
    [0 0 1 1 1] 7))

(deftest test-sum-fraction-bits
  (are [digits expected]
       (= expected (#'i/sum-fraction-bits digits))
    [0, 1 0 0 0] 1/2
    [0, 0 1 0 0] 1/4
    [1, 1 0 0 0] 3/2
    [0, 1 1]     3/4))

(deftest test-zeros?
  (are [ds expected]
       (= expected (#'i/zeros? ds))
    [0 0] true
    [0 1 0] false
    [0 0 1] false))

(deftest test-ones?
  (are [ds expected]
       (= expected (#'i/ones? ds))
    [1 1] true
    [0 1] false
    [1 1 0] false))

(deftest test-type
  (are [bits expected]
       (= expected (#'i/type bits))
    [[1] [1 1] [0 0]] :infinity
    [[1] [1 1] [1 0]] :nan
    [[1] [0 0] [0 0]] :zero
    [[1] [0 0] [0 1]] :subnormal
    [[1] [0 1] [0 1]] :normal))

(deftest test-exponent-attrs
  (are [ks expected]
       (= expected (#'i/exponent-attrs ks))
    {:float-type :zero}
    {:exponent 0, :exponent-biased 0}

    {:float-type :normal
     :exponent-bias -127
     :exponent-bits [0 1 1 1 1 1 1 1]
     :fraction-bits [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]}
    {:exponent 0
     :exponent-biased 127}

    {:float-type :nan}
    {}

    {:float-type :infinity}
    {}))

(deftest test-fraction-attrs
  (are [ks expected]
       (= expected (#'i/fraction-attrs ks))
    {:float-type :zero}
    {:fraction 0}

    {:float-type :normal
     :sign 0
     :fraction-bits [1 0 0]}
    {:fraction 3/2}

    {:float-type :normal
     :sign 1
     :fraction-bits [0 1 0]}
    {:fraction 5/4}

    {:float-type :subnormal
     :sign 0
     :fraction-bits [1 0 0]}
    {:fraction 1/2}

    {:float-type :nan}
    {}

    {:float-type :infinity}
    {}))

