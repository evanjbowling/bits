(ns com.evanjbowling.bits
  (:require
   [com.evanjbowling.bits.impl :as impl]))

(def char-bits
  "Returns big-endian sequence of binary integer values
  representing UTF-16 encoding of char. Accepts char
  and string."
  impl/char-bits)
(def from-char-bits
  "Convert binary sequence to char"
  impl/from-char-bits)

(def short-bits
  "Returns big-endian sequence of binary integer values
  representing twos-complement signed encoding of short.
  Accepts short and string."
  impl/short-bits)
(def from-short-bits
  "Convert binary sequence to short"
  impl/from-short-bits)

(def int-bits
  "Returns big-endian sequence of binary integer values
  representing twos-complement signed encoding of int.
  Accepts int and string."
  impl/int-bits)
(def from-int-bits
  "Convert binary sequence to int"
  impl/from-int-bits)

(def long-bits
  "Returns big-endian sequence of binary integer values
  representing twos-complement signed encoding of long.
  Accepts long and string."
  impl/long-bits)
(def from-long-bits
  "Convert binary sequence to long"
  impl/from-long-bits)

;;
;; floats
;;

(def float-bits
  "Returns big-endian sequence of binary integer values
  representing ieee754 binary 32-bit encoding of float.
  Sequence is formatted like:
    [[sign bit] [exponent bits] [fraction bits]]
  Accepts float and string."
  impl/float-bits)
(def from-float-bits
  "Convert binary sequence to float"
  impl/from-float-bits)
(def float-attrs
  "Returns attributes of ieee754 binary 32-bit float"
  impl/float-attrs)

(def double-bits
  "Returns big-endian sequence of binary integer values
  representing ieee754 binary 64-bit encoding of double.
  Sequence is formatted like:
    [[sign bit] [exponent bits] [fraction bits]]
  Accepts double and string."
  impl/double-bits)
(def from-double-bits
  "Convert binary sequence to double"
  impl/from-double-bits)
(def double-attrs
  "Returns attributes of ieee754 binary 64-bit double"
  impl/double-attrs)

