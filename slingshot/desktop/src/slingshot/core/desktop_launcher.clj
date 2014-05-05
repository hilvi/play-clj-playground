(ns slingshot.core.desktop-launcher
  (:require [slingshot.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. slingshot "slingshot" 800 600)
  (Keyboard/enableRepeatEvents true))
