(ns great-rpg.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(def movement-speed 0.5)

(defn get-player [entities]
  (some #(if (:player? %) %) entities))

(defn move [entity direction]
  (let [{x :x, y :y} entity
        entity (assoc entity :old-x x :old-y y)]
    (case direction
      :up (assoc entity :y (+ y movement-speed))
      :down (assoc entity :y (- y movement-speed))
      :left (assoc entity :x (- x movement-speed))
      :right (assoc entity :x (+ x movement-speed)))))

(defn on-obstacle? [screen entity layer]
  (let [{x :x, y :y} (screen->isometric screen entity)
        layer (tiled-map-layer screen layer)]
    (not (nil? (tiled-map-cell layer x y)))))

(defn prevent-move [screen entity]
  (let [{x :old-x, y :old-y} entity]
    (if (on-obstacle? screen entity "obstacles")
      (assoc entity :x x :y y)
      entity)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :renderer (isometric-tiled-map "level.tmx" (/ 1 64))
                          :camera (orthographic))
          sheet (texture "64x64.png")
          tiles (texture! sheet :split 64 64)
          player (texture (aget tiles 10 0))
          player (assoc player :x 1 :y 0 :width 1 :height 1 :player? true)]
      (position! screen 0 0)
      [player]))
  :on-render
  (fn [screen entities]
    (let [player (get-player entities)
          {x :x, y :y} player]
      (clear!)
      (position! screen x y)
      (render! screen entities)))
  :on-resize
  (fn [screen entities]
    (height! screen 8))
  :on-key-down
  (fn [screen entities]
    (let [player (get-player entities)]
      (prevent-move screen (cond
       (= (:keycode screen) (key-code :dpad-right))
       (move player :right)
       (= (:keycode screen) (key-code :dpad-down))
       (move player :down)
       (= (:keycode screen) (key-code :dpad-up))
       (move player :up)
       (= (:keycode screen) (key-code :dpad-left))
       (move player :left))))))



(defgame great-rpg
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
