package inkball;

import processing.core.PImage;

public abstract class Object {
    public float x;
    public float y;

    public PImage sprite;



    public App mainApp;

    public abstract void draw();

    public abstract void setSprite(PImage sprite);


}
