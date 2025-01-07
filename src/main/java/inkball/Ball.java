package inkball;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PFont;
import java.util.Random;

@SuppressWarnings("unused")
public class Ball extends Object{








    public Colours.c colour = Colours.c.Grey;


    public double centreX;
    public double centreY;
    public float radius = 16;


    public float xSize =24;
    public float ySize =24;
    public float offset;


    public double iVelocity;
    public double jVelocity;

    

    public Ball(float x, float y, App mainApp){
        this.x = x;
        this.y = y;
        this.mainApp = mainApp;

        
        Random random = new Random();

        iVelocity = getRandomDirection(random.nextInt(2));
        jVelocity = getRandomDirection(random.nextInt(2));

        // jVelocity = 2;
        // iVelocity = 2;
    }


    public void setSprite(PImage sprite){
        this.sprite = sprite;
    }

    public void draw() {
        //Tick
        if(mainApp.ispaused == false && mainApp.isEnd  == false){
        movePosition();
        centreX = x +16 - 5 +iVelocity;
        centreY = y +16 - 5+ jVelocity;
        }
        
        //Draw
        mainApp.image(this.sprite, this.x + offset, this.y + offset,xSize,ySize);
        

    }

    void movePosition(){



        x += iVelocity;
        y += jVelocity;
    }

    public void scaleBall(double distance){
        float size = 24;

        size = (float)(distance/32) *24;
        xSize = size;
        ySize = size;

        offset = (24 -size) *0.5f;
    }

    public void change_colour(PImage sprite, Colours.c colour){
        setSprite(sprite);
        this.colour = colour;
    }

    private float getRandomDirection(int rand){
        if(rand == 0){
            return 2;
        }
        if(rand == 1){
            return -2;
        }
        
        return 0;
    }
}
