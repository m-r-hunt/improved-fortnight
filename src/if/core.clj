(ns if.core
  (:require if.house
            [if.systems :as ifs]))

(defonce ^:private cur-state (atom {:cur-pos :if.house/study
                                    :world {:if.house/study (ifs/get-room :if.house/study)}
                                    :objects (ifs/get-objects-in :if.house/study)
                                    :flags {}}))

(defn -main
  []
  (print cur-state))

(defn load-room!
  [room]
  (swap! cur-state assoc-in [:world room] (ifs/get-room room))
  (swap! cur-state update :objects merge (ifs/get-objects-in room)))

(defn reload-room!
  [room]
  (swap! cur-state assoc-in [:world room] (ifs/get-room room)))

(defn reload-all-rooms!
  []
  (swap! cur-state
         assoc :world (merge (:world @cur-state)
                             (filter #(contains? (:world @cur-state) (first %))
                                     (ifs/get-rooms)))))

(defn reload-object!
  [object]
  (swap! cur-state assoc-in [:objects object] (ifs/get-object object)))

(defn reload-all-objects!
  []
  (swap! cur-state
         assoc :objects (merge (:objects @cur-state)
                             (filter #(contains? (:objects @cur-state) (first %))
                                     (ifs/get-objects)))))

(defn current-room
  []
  (get-in @cur-state [:world (:cur-pos @cur-state)]))

(defn current-objects
  []
  (filter #(= (get-in % [1 :location]) (:cur-pos @cur-state))
          (:objects @cur-state)))

(defn describe-objects
  []
  (apply str (interpose " " (map #(str "There is a " (get-in % [1 :pretty-name]) " here.")
                           (current-objects)))))

(defn describe-exits
  []
  (apply str (interpose " " (map #(str "There is a "
                                 (first (second %))
                                 " leading "
                                 (name (first %))
                                 ".")
                           (get (current-room) :exits)))))

(defn look
  []
  (str (:pretty-name (current-room)) "\n"
       (:description (current-room)) " "
       (describe-objects) " "
       (describe-exits)))

(defn do-scriptable-action
  [key action & args]
  (if-let [script (ifs/get-script key)]
    (let [[res new-state string] (script @cur-state)]
      (reset! cur-state new-state)
      (if res
        (str string " " (apply action args))
        string))
    (apply action args)))

(defn acually-go
  [direction portal new-room]
    (do
      (swap! cur-state assoc :cur-pos new-room)
      (if-not (get-in [:world new-room] @cur-state)
        (load-room! new-room))
      (str "You go " (name direction)
           " through the " portal ".\n" (look))))

(defn go!
  [direction]
  (if-let [[portal new-room] (get (:exits (current-room)) direction)]
    (do-scriptable-action [:go (:cur-pos @cur-state) direction]
                          acually-go
                          direction portal new-room)
    "You cannot go that way."))

(defn get!
  [object]
  (if (= (:cur-pos @cur-state) (get-in @cur-state [:objects object :location]))
    (swap! cur-state assoc-in [:objects object :location] :inventory)
    "That isn't here."))

(defn use!
  [object target]
  (do-scriptable-action [:use object target] #()))

(defn warp-to!
  [room]
  (swap! cur-state assoc :cur-pos room))
