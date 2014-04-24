(ns tilemap.core.desktop-launcher
  (:require [tilemap.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. tilemap "tilemap" 600 600)
  (Keyboard/enableRepeatEvents true))
