(ns if.house
  (:require [if.systems :as ifs]))

(ifs/defroom ::study
  "The Study"
  "Max's study, where he spends all his free time."
  {:east ["door" ::hallway]
   :north ["window" ::outside]})

(ifs/defobject ::computer
  "Computer"
  "Max's gaming PC. It's a bit noisy when first switched on."
  ::study)

(ifs/defobject ::bass
  "Bass Guitar"
  "A creamy orange Fender Jazz Bass."
  ::study)

(ifs/defobject ::breastplate
  "Breast Plate"
  "A breast plate from a suit of medival armour. On closer inspection it's made of foam."
  ::boiler-cupboard)

(ifs/defroom ::hallway
  "Ground Floor Hallway"
  "A small hallway."
  {:north ["arch" ::kitchen]
   :south ["arch" ::living-room]})

(ifs/defroom ::living-room
  "The Living Room"
  "The main living room."
  {:north ["arch" ::hallway]
   :south ["front door" ::outside]})

(ifs/defobject ::key
  "The Front Door Key"
  "The key to the front door."
  ::living-room)

(ifs/defflag ::front-door-locked true)

(ifs/defscript
  [:use ::key ::front-door]
  [true (ifs/set-flag state ::front-door-locked false) "You unlock the door."])

(ifs/defscript
  [:go ::living-room :south]
  (if (ifs/flag? state ::front-door-locked)
    [false state "You can't go out because the door is locked."]
    [true state "You go out the unlocked door."]))
