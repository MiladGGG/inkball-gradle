package inkball;

import processing.core.PImage;

import org.checkerframework.checker.units.qual.C;

import processing.core.PApplet;
import processing.core.PFont;

@SuppressWarnings("unused")
public class Tile extends Object implements Collision{





    public int arrayInd;

    public Colours.c colour = Colours.c.Grey;

    //Tile bools
    public boolean collisionEnabled;
    boolean is_futile;
    boolean ball_override;

    //Spawner
    public boolean isSpawner;


    //HOLE TILES
    public boolean isHole;
    public int holeCentreX;
    public int holeCentreY;
    float holeDistance = 10f;

    enum collide_direction {
        x,
        y
    }

    collide_direction direction;

    //Points
    Point[] point = new Point[4];

    LinePoint[] side = new LinePoint[4];


    public Tile(int x, int y, App mainApp){
        this.x = x;
        this.y = y;
        this.mainApp = mainApp;
        
        holeCentreX = x +32;
        holeCentreY = y + 32;
    }

    public void setSprite(PImage sprite){
        if(is_futile == false && ball_override == false)
        this.sprite = sprite;
    }


    public void draw() {
        //Tick
        if(mainApp.ispaused == false && mainApp.isEnd == false){
            CollisionReport report = checkCollision();
            if(report.collision_occured){
                report.ball.iVelocity = report.ux;
                report.ball.jVelocity = report.uy;
                if(colour != Colours.c.Grey){
                    report.ball.change_colour(mainApp.ballCols[Colours.getSpriteIndex(colour)], colour);
                }
            }
            
    
            if(isHole)
            holeSense();
        }



        //Draw
        mainApp.image(this.sprite, this.x, this.y);
        

            

        

    }


    public CollisionReport checkCollision(){

        //Move to initialisation
        
        for(int i = 0; i < 4; i++)
        point[i] = new Point(i,x,y);

        for(int i = 0; i < 4; i++){
            side[i] = getCorner(i);
        }

        if(collisionEnabled){
            for(int j = 0; j< mainApp.balls.size(); j++){
                
                
                Ball ball =  mainApp.balls.get(j);

                Boolean withinY = false;
                Boolean withinX = false;
                

                double posx = ball.centreX - ball.iVelocity;
                double posy = ball.centreY - ball.jVelocity;


                //WITHIN Y FILE
                if(posx > side[0].x1  && posx < side[0].x2 ){
                    withinY = true;
                }
                else{
                    withinY = false;
                }
                //WITHIN X FILE
                if(posy > side[0].y1  && posy < point[3].y ){
                    withinX = true;
                }
                else{
                    withinX = false;
                }
            

                if(withinX && withinY){
                    if(direction == collide_direction.x)
                    return new CollisionReport(true, ball.iVelocity * -1, ball.jVelocity, ball);
                    if(direction == collide_direction.y)
                    return new CollisionReport(true, ball.iVelocity, ball.jVelocity * -1, ball);
                }

                
                if(withinX && withinY == false){
                    direction = collide_direction.x;
                }
                if(withinY && withinX == false){
                    direction = collide_direction.y;
                }
                


                
            }
            
        }

        //Return
        return new CollisionReport(false);
    }


    public void holeSense(){
        for(int j = 0; j< mainApp.balls.size(); j++){
            Ball ball = mainApp.balls.get(j);

            double distance1 = Math.sqrt(Math.pow((ball.centreX - holeCentreX),2) + Math.pow((ball.centreY - holeCentreY),2));


    
                if(distance1 < 32){
                    //Edge closer
                    double xVector = holeCentreX- ball.centreX;
                    double yVector = holeCentreY - ball.centreY;

                    ball.iVelocity += xVector * 0.005f;
                    ball.jVelocity += yVector * 0.005f;

                    ball.scaleBall(distance1);

                    if(distance1 < holeDistance){
                        mainApp.ballHole(ball, this);
                    }
                }
            }
    }


    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }

    public void setX(int i){
        this.x = i;
    }
    public void setY(int i ){
        this.y = i;
    }

    public void banish(){
        is_futile = true;
        sprite = null;
    }
    public void ball_wall(PImage wall){
        setSprite(wall);
        ball_override = true;
        
    }

    public LinePoint getCorner(int index){
        if(index == 0){
            return new LinePoint(point[0].x,point[1].x,point[0].y,point[1].y);
        }
        if(index == 1){
            return new LinePoint(point[0].x,point[2].x,point[0].y,point[2].y);
        }
        if(index == 2){
            return new LinePoint(point[2].x,point[3].x,point[2].y,point[3].y);
        }
        if(index == 3){
            return new LinePoint(point[1].x,point[3].x,point[1].y,point[3].y);
        }
        return null;
    }



    public String speakCorner(int index){
        if(index == 0){
            return "UP";
        }
        if(index == 1){
            return  "LEFT";
        }
        if(index == 2){
            return "DOWN";
        }
        if(index == 3){
            return "RIGHT";
        }
        return null;
    }

class Point{
    public float x;
    public float y;

    public Point(int Corner, float x, float y ){

        if(Corner == 0 || Corner == 2){
            this.x = x;
        }
        if(Corner == 1 || Corner == 3){
            this.x = x + 32;
        }
        if(Corner == 0 || Corner == 1){
            this.y = y;
        }
        if(Corner == 2 || Corner == 3){
            this.y = y + 32;
        }
        
    }


    }
}
