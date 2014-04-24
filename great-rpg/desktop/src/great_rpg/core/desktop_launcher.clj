(ns great-rpg.core.desktop-launcher
  (:require [great-rpg.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. great-rpg "great-rpg" 800 400)
  (Keyboard/enableRepeatEvents true))
