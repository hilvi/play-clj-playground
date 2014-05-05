(ns slingshot.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))

(defn move-camera-to [screen entity]
  (let [{x :x y :y} entity]
    (position! screen x y)))

(defn move-to [entity input]
  (let [x (- (:x input) (/ (:width entity) 2.0))
        y (- (:y input) (/ (:height entity) 2.0))]
    (assoc entity :x x :y y)))

(defn get-throwable [entities]
  (some #(if (:throwable? %) %) entities))

(defn drag-in-progress? [throwable]
  (if (:p throwable)
    true
    false))

(defn current-drag? [throwable pointer]
  (if (drag-in-progress? throwable)
    (== (:p throwable) pointer)
    false))

(defn center [entity]
  (let [x (+ (:x entity) (/ (:width entity) 2.0))
        y (+ (:y entity) (/ (:height entity) 2.0))]
    {:x x :y y}))

(defn cursor-inside? [entity {x-in :x y-in :y}]
  (let [{w :width h :height x :x y :y} entity]
    (and (> x-in x) (> y-in y)
         (< x-in (+ x w)) (< y-in (+ y h)))))

(defn magnitude [[x y]]
  (Math/sqrt (+ (* x x) (* y y))))

(defn distance [vec1 vec2]
  (let [[x1 y1] vec1
        [x2 y2] vec2
        x (- x1 x2)
        y (- y1 y2)]
    (magnitude [x y])))

(defn normalize [vec1]
  (let [[x y] vec1
        mag (magnitude vec1)]
    [(/ x mag) (/ y mag)]))

(defn apply-forces [{delta :delta-time} throwable]
  (let [{vel-y :vel-y vel-x :vel-x
         x :x y :y} throwable
        throwable (assoc throwable :vel-y (- vel-y (* delta 20)))]
    (assoc throwable :x (+ x (* delta vel-x)) :y (+ y (* delta vel-y)))))

(defn sling [throwable input]
  (let [multiplier 3
        vel-x (* (- (:start-x throwable) (:x input)) multiplier)
        vel-y (* (- (:start-y throwable) (:y input)) multiplier)]
    (assoc throwable :throw? true :vel-y vel-y :vel-x vel-x)))

(defn reset [throwable]
  (assoc throwable :x 20 :y 20 :throw? false))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage)
             :camera (orthographic))
    [(assoc (texture "Crate.png") :x 20 :y 20 :width 5 :height 5 :throwable? true)])
  :on-render
  (fn [screen entities]
    (let [throwable (get-throwable entities)]
      (clear!)
      (->> entities
           (map (fn [entity]
                  (if (:throw? entity)
                    (apply-forces screen entity)
                    entity)))
           (render! screen))))
  :on-resize
  (fn [screen entities]
    (height! screen 100))
  :on-touch-down
  (fn [screen entities]
    (let [throwable (get-throwable entities)
          {p :pointer x-in :input-x y-in :input-y} screen
          input (input->screen screen x-in y-in)
          c (center throwable)
          last (if (:last throwable)
                 (:last throwable) 0)
          time (:total-time screen)]
      (cond
        (and
         (not (:throw? throwable))
         (not (drag-in-progress? throwable))
         (cursor-inside? throwable input))
        (assoc (move-to throwable input) :p p :start-x (:x c) :start-y (:y c))
        (> 0.2 (- time last))
        (reset throwable)
        :else
        (assoc throwable :last time))))
  :on-touch-dragged
  (fn [screen entities]
    (let [throwable (get-throwable entities)
          {p :pointer x-in :input-x y-in :input-y} screen
          input (input->screen screen x-in y-in)]
      (if (current-drag? throwable p)
        (move-to throwable input))))
  :on-touch-up
  (fn [screen entities]
    (let [throwable (get-throwable entities)
          {p :pointer x :input-x y :input-y} screen]
      (if (current-drag? throwable p)
        (sling (dissoc throwable :p) (input->screen screen x y)))))
  :on-key-down
  (fn [screen entities]
    (let [throwable (get-throwable entities)]
      (cond
        (= (:keycode screen) (key-code :space))
        (reset throwable)))))

(defgame slingshot
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
