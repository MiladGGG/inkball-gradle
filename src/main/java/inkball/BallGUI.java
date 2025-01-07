package inkball;

import processing.core.PImage;

public class BallGUI extends Object{

    public float offset;

    public PImage sprite;
    public Boolean isNext = false;

    BallGUI(float x, float y,PImage sprite, App mainApp){
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.mainApp =  mainApp;
    }

    public void draw(){
        
        if(isNext){
            offset =  x - 20;

            if(x > 20){
                for(int i =0; i< mainApp.GUIball.size(); i ++){
                    mainApp.GUIball.get(i).moveLeft();  
                }

            }
        }

        mainApp.image(sprite, x, y, 30 ,30);
    }

    public void setSprite(PImage sprite){
        this.sprite = sprite;
    }


    public void moveLeft(){
        if(mainApp.ispaused == false)
        x-=1;
    }
}
