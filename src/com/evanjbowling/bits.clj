(ns com.evanjbowling.bits
  (:require
   [com.evanjbowling.bits.impl :as impl]))

;; TODO: doc strings

(def char-bits impl/char-bits)
(def from-char-bits impl/from-char-bits)
(def short-bits
  "Returns big-endian sequence of 0/1 integer values
  representing twos-complement signed encoding."
  impl/short-bits)
(def from-short-bits impl/from-short-bits)
(def int-bits impl/int-bits)
(def from-int-bits impl/from-int-bits)
(def long-bits impl/long-bits)
(def from-long-bits impl/from-long-bits)

;;
;; floats
;;

(def float-bits impl/float-bits)
(def from-float-bits impl/from-float-bits)

(def double-bits impl/double-bits)
(def from-double-bits impl/from-double-bits)

(def float-attrs impl/float-attrs)
(def double-attrs impl/double-attrs)

