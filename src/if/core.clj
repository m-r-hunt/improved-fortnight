(ns if.core
  (:require if.house
            [if.systems :as ifs]))

(defonce ^:private cur-state (atom {:cur-pos :if.house/study
                                :world {:if.house/study (ifs/get-room :if.house/study)}
                                :objects (ifs/get-objects-in :if.house/study)}))

(defn -main
  []
  (print cur-state))

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

(defn look
  []
  (:description (current-room)))

(defn acually-go
  [direction portal new-room]
    (do
      (swap! cur-state assoc :cur-pos new-room)
      (str "You go " direction " through the " portal " to get to " new-room)))

(defn go!
  [direction]
  (let [res-string (if-let [exit (get (:exits (current-room)) direction)]
                     (if-let [script (ifs/get-script [:go (:cur-pos @cur-state) direction])]
                       (let [[res new-state string] (script @cur-state)]
                         (reset! cur-state new-state)
                         (if res
                           (str string " " (acually-go direction (first exit) (second exit)))
                           string))
                       (acually-go direction (first exit) (second exit)))
                     "You cannot go that way.")]
    (if-not (get-in [:world (:cur-pos @cur-state)] @cur-state)
      (reload-room! (:cur-pos @cur-state)))
    res-string))

(defn warp-to!
  [room]
  (swap! cur-state assoc :cur-pos room))
