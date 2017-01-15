(ns if.systems)

;; TODO Refactor these systems with a meta-macro. They are structurally similar.

(defonce ^:private rooms (atom {}))

(defn add-room
  [name room]
  (swap! rooms assoc name room))

(defmacro defroom
  [name pretty-name description exits]
  `(add-room ~name {:pretty-name ~pretty-name
                    :description ~description
                    :exits ~exits}))

(defn get-room
  [name]
  (get @rooms name))

(defn get-rooms
  []
  @rooms)

(defonce ^:private objects (atom {}))

(defn add-object
  [name object]
  (swap! objects assoc name object))

(defmacro defobject
  [name pretty-name description start-loc]
  `(add-object ~name {:pretty-name ~pretty-name
                      :description ~description
                      :location ~start-loc}))

(defn get-object
  [object]
  (get @objects object))

(defn get-objects
  []
  @objects)

(defn get-objects-in
  [room]
  (conj {} (filter #(= room (:location (second %))) @objects)))

(defonce ^:private flags (atom {}))

(defn add-flag
  [key default]
  (swap! flags assoc key default))

(defmacro defflag
  [key default]
  `(add-flag ~key ~default))

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

(defn set-flag
  [state key value]
  (assoc-in state [:flags key] value))

(defn flag?
  [state key]
  (if-not (nil? (get-in state [:flags key]))
    (get-in state [:flags key])
    (get @flags key)))
