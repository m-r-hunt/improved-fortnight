;; ## Systems that underlie the Engine.
;;
;; The general theme of this ns is that you have a defthing macro which registers
;; thingies in the registry. These thingies will then be loaded when required by
;; the game runtime. Re-evaluating the defthing form will update the registry
;; but not the running game, until you ask the runtime to reload it. This means
;; you can rewrite and eval code without messing up the state of a running game
;; until you want the updated code to run.
;;
(ns if.systems)

(defmacro system
  [sys & args]
  (let [registry (symbol (str (name sys) "s"))
        add-sys! (symbol (str "add-" (name sys) "!"))
        fn-args (interleave (map keyword args) args)]
    `(do
       (defonce ^:private ~registry (atom {}))
       (defn ~add-sys!
         [~'key ~'val]
         (swap! ~registry assoc ~'key ~'val))
       (defmacro ~(symbol (str "def" (name sys)))
         [~'key ~@args]
         `(~~add-sys! ~~'key ~(hash-map ~@fn-args)))
       (defn ~(symbol (str "get-" (name sys)))
         [~'key]
         (get @~registry ~'key))
       (defn ~(symbol (str "get-" (name sys) "s"))
         []
         @~registry))))

(system room pretty-name description exits)

(system object pretty-name description location)

(defn get-objects-in
  [room]
  (conj {} (filter #(= room (:location (second %))) @objects)))

(system flag default)

(defn set-flag
  [state key value]
  (assoc-in state [:flags key] value))

(defn flag?
  [state key]
  (if-not (nil? (get-in state [:flags key]))
    (get-in state [:flags key])
    (get-in @flags [key :default])))

;; The scripts system requires different code due to the implicit function in
;; the defscript macro. Thus, we write out the code by hand.
(defonce ^:private scripts (atom {}))

(defn add-script
  [key f]
  (swap! scripts assoc key f))

(defmacro defscript
  [key body]
  `(add-script ~key (fn [~'state] ~body)))

(defn get-script
  [key]
  (get @scripts key))
