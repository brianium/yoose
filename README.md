# yoose

<p align="center">
  <img src="https://raw.github.com/brianium/yoose/master/yussss.gif" alt="Finn Says Yussssss" />
</p>

Yoose attempts to encourage use case centered applications.

*What is a use case?*

> In software and systems engineering, a use case is a list of actions or event steps typically defining the interactions between a role and a system to achieve a goal. The actor can be a human or other external system.

Yoose attempts to define a use case as a process created with input and output ports (currently via core.async channels). A use case should be able to gather input from it's input port to accomplish it's goal, and send output to it's output port to notify interested parties. Interested parties you say??!?!? An interested party might be an HTTP handler, a CLI application, a robotic arm? - the list goes on and on.

## Usage

The following contrived example is inspired by the [clean todos](https://github.com/brianium/clean-todos) project.

```clojure
(ns yoose.todos
  (:require [yoose.async :refer :all]))
  
(defusecase create-todo [this db]
  (let [entity (<in this)]
    (->> entity
         (save db)
         (create-message :todo/create)
         (>out this))))
```

The `defusecase` is an optional bit of syntactic sugar, but one I find useful. `defusecase` defines a function
tha is used for creating use cases. A `use-case-factory` if you will. These factories expect to be called
with an input and output channel a la `core.async`, and any other dependencies needed to do the job. Or in `clojure.spec` parlance:

```clojure
(s/def ::use-case-factory
  (s/fspec :args (s/cat
                   :in   ::in
                   :out  ::out
                   :deps (s/* any?))
           :ret  yoose.spec/use-case))
```

And so one might actually create an instance of a use case like so:

```clojure
(def input (chan))

(def output (chan))

(def db (create-a-db))

(def use-case (create-todo input output db))
```

Notice how dependencies defined after `this` in the `defusecase` example are passed in after `input` and `output`.

Any number of additional arguments can be defined this way:

```clojure
(defusecase create-todo [this db arg2 arg3] ...)
```

While this is possible, I would recommend a single dependency map. This is super convenient when passing
around dependencies - say with a super cool library like [mount](https://github.com/tolitius/mount) - and destructuring is always your friend.

```clojure
(defusecase create-todo [this {:keys [db arg2 arg3]}] ...)
```

**Note: use cases are currently built with `core.async` in mind, but it may not always be the case. It's possible
that some day `yoose.manifold` or `yoose.queue` might burst onto the scene and define use case factories with different expectations. When in doubt - refer to the specs ;)


## Documentation

### yoose.core

`yoose.core` defines the api for exercising use cases. A use case is something that implements the `yoose.core/UseCase`
protocol. Most of the functions in `yoose.core` just implement the functions defined by the protocol.


**push!**

Places a value into the use case input port

```clojure
(push! use-case "some value!")
```

**pull!**

Calls the given function with the next value taken from the output port

```clojure
(pull! use-case #(println %))
```

**pull!!**

Takes a value from the output port and returns it. Blocks until output is received

```clojure
(def response (pull!! use-case))
```

**<in**

Takes a value from the input port. The use of this function is encouraged only in the context of defining a use case

```clojure
(let [input (<in use-case)])
```

**>out**

Puts a value into the output port. The use of this function is encouraged only in the context of defining a use case

```clojure
(>out use-case "an output message")
```

**in**

Returns the input port of the use case

```clojure
(let [port (in use-case)])
```

**out**

Returns the output port of the use case

```clojure
(let [port (out use-case)])
```

**close!**

Closes input and output ports

```clojure
(close! use-case)
```

**trade!!**

Pushes a values into the use case and blocks until output is available.

```clojure
(def result (trade!! use-case "hello"))
```

**use-case?**

Check if the given value implements the `yoose.core/UseCase` protocol

```clojure
(use-case? value)
```

For more information - see the [spec](https://github.com/brianium/yoose/blob/master/src/yoose/spec.clj)

##yoose.async

Provides a `core.async` implementation of the `yoose.core/UseCase` protocol.

**make-use-case**

Creates a new use case backed by `core.async`

```clojure
(require '[clojure.core.async :refer [chan]])

(def input (chan))

(def output (chan))

(def use-case (make-use-case input output))
```

**<in**

`yoose.core/<in` redefined as a macro. Since `go` macro translation stops at function creation boundaries - `yoose.core/<in` can't opt into a wrapping `go` block. Using `yoose.async/<in` circumvents this problem.

```clojure
;;; BAD - throws error for using <! outside of go block
(go
  (let [input (yoose.core/<in use-case)]))
  
;;; OK
(go
  (let [input (yoose.async/<in use-case)]))
```

**>out**

`yoose.core/>out` redefined as a macro. See `yoose.core/<in` rationale above.


**defusecase**

Defines an async use case. A use case is really just a function
that executes it's body in the context of a go loop.

```clojure
(defusecase create-todo [this db]
  (let [entity (<in this)]
    (->> entity
         (save db)
         (create-message :todo/create)
         (>out this))))
		 
;; is equivalent to

(defn create-todo [in out db]
  (let [use-case (make-use-case in out)]
    (go-loop []
      (let [entity (<in this)]
        (->> entity
             (save db)
             (create-message :todo/create)
             (>out this)))
    (recur))
  use-case))
```
