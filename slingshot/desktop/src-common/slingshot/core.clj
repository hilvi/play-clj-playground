(ns slingshot.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(defn move-camera-to [screen entity]
  (let [{x :x y :y} entity]
    (position! screen x y)))

(defn get-throwable [screen entities]
  (some #(if (:throw? %) %) entities))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)
                        :camera (orthographic))
    [(assoc (texture "Crate.png") :x 0 :y 0 :width 5 :height 5 :throw? true)])
  :on-render
  (fn [screen entities]
    (clear!)
    (move-camera-to screen (get-throwable screen entities))
    (render! screen entities))
  :on-resize
  (fn [screen entities]
    (height! screen 100)))

(defgame slingshot
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
