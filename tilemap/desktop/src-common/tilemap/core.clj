(ns tilemap.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(def size 6)
(def world [[[0 0] [0 0] [0 0] [0 0] [0 0] [4 4]]
            [[0 0] [3 8] [4 8] [0 0] [4 3] [0 0]]
            [[0 0] [3 9] [4 9] [0 0] [0 0] [0 0]]
            [[0 3] [0 0] [0 0] [8 5] [9 5] [10 5]]
            [[0 4] [0 0] [4 3] [8 6] [9 6] [10 6]]
            [[0 5] [0 0] [0 0] [0 0] [0 0] [0 0]]])

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)
             :camera (orthographic :set-to-ortho true))
    (let [sheet (texture "tilemap.bmp")
          tiles (texture! sheet :split 20 20)]
      (for [y (range (count world))
            x (range (count (first world)))]
        (let [coord (get-in world [y x])]
        (assoc (texture (aget tiles (second coord) (first coord)) :flip false true)
          :x x :y y :width 1 :height 1)))))
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  :on-resize
  (fn [screen entities]
    (height! screen size)
    nil))

(defgame tilemap
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
