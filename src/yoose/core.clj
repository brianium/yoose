(ns yoose.core)


(defprotocol UseCase
  (-push!  [this value] "Places a value into the use case input port")
  (-pull!  [this fn1-handler] "Calls the given function with the next value taken from the output port")
  (-pull!! [this] "Takes a value from the output port and returns it. Blocks")
  (-<in [this] "Reads a value from the input port")
  (->out [this value] "Puts a value into the output port")
  (-in [this] "Returns the input port of the use case")
  (-out [this] "Returns the output port of the use case")
  (-close! [this] "Closes input and output ports"))


(defn use-case?
  "Returns true if the given value is a UseCase. Returns false otherwise"
  [value]
  (satisfies? UseCase value))


(defn push!
  "Places a value into the use case input port"
  [use-case value]
  (-push! use-case value))


(defn pull!
  "Calls the given function with the next value taken from the output port"
  [use-case fn1-handler]
  (-pull! use-case fn1-handler))


(defn pull!!
  "Takes a value from the output port and returns it. Blocks"
  [use-case]
  (-pull!! use-case))


(defn <in
  "Takes a value from the input port. Should only be used when defining a use-case"
  [use-case]
  (-<in use-case))


(defn >out
  "Puts a value into the output port. Should only be used when defining a use-case"
  [use-case value]
  (->out use-case value))


(defn in
  "Returns the input port of the use case"
  [use-case]
  (-in use-case))


(defn out
  "Returns the output port of the use case"
  [use-case]
  (-out use-case))


(defn close!
  "Closes input and output ports"
  [use-case]
  (-close! use-case))


(defn trade!!
  "Pushes a value into the use case and blocks until output is available"
  [use-case value]
  (-> use-case
      push! value
      pull!!))
