# bits

A smallish Clojure library for transforming Java primitives into binary sequences and back again. Intended to help explore encodings. Particularly useful for `float` and `double` types.

[![Build Status](https://travis-ci.org/evanjbowling/bits.svg?branch=master)](https://travis-ci.org/evanjbowling/bits)
[![Dependencies Status](https://versions.deps.co/evanjbowling/bits/status.svg)](https://versions.deps.co/evanjbowling/bits)

[![Clojars Project](https://img.shields.io/clojars/v/com.evanjbowling/bits.svg)](https://clojars.org/com.evanjbowling/bits)

## Install

Leiningen:

```clojure
[com.evanjbowling/bits "0.0.1"]
```

## Quick Demo

Start a REPL with this lib (WARN: only do this for dependencies you trust!):

```
clj -Sdeps '{:deps {com.evanjbowling/bits {:mvn/version "0.0.1"}}}'
```

Load the namespace:

```clojure
(require '[com.evanjbowling.bits :as bits])
```

View the binary encoding for a few `short`s:

```clojure
(require '[clojure.pprint :as pp])
(require '[clojure.string :as str])

(defn print-shorts [shorts]
  (let [max-s (apply max (map (comp count str) shorts))]
    (doseq [s shorts]
      (let [padded-val (pp/cl-format nil (str "~" max-s "@a") s)
            ->str (fn[x](str/join "" x))]
        (printf "%s %s\n"
          padded-val
          (->str (bits/short-bits (short s))))))))

(print-shorts [Short/MIN_VALUE -1 0 1 Short/MAX_VALUE])
-32768 1000000000000000
    -1 1111111111111111
     0 0000000000000000
     1 0000000000000001
 32767 0111111111111111
```

Examine some `char`s:

```clojure


(->> ["A" "a" "µ"]
     (map bits/char-bits)
     clojure.pprint/pprint)

; ((0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 1)
;  (0 0 0 0 0 0 0 0 0 1 1 0 0 0 0 1)
;  (0 0 0 0 0 0 0 0 1 0 1 1 0 1 0 1))
```

The floating point bit sequences are grouped [[_sign_]  [_exponent_] [_fraction_]]:

```clojure
(bits/float-bits "0.25")

; ([0] [0 1 1 1 1 1 0 1] [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0])
```

You can convert the bit sequence back to it's original form:

```clojure
(bits/from-float-bits '([0] [0 1 1 1 1 1 0 1] [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]))

; 0.25
```

This is a great way to see what happens when you make the exponent all ones:

```clojure
(bits/from-float-bits '([0] [1 1 1 1 1 1 1 1] [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]))

; Infinity
```

And then tweak the fraction bits too:

```clojure
(bits/from-float-bits '([0] [1 1 1 1 1 1 1 1] [1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]))

; NaN
```

## License

Copyright © 2019-2024 Evan Bowling

Distributed under the MIT License
