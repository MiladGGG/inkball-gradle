package inkball;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PFont;

import java.io.*;
import java.util.*;


@SuppressWarnings("unused")
public class Line implements Collision {
    public LinkedList<LinePoint> segments = new LinkedList<LinePoint>();

    App mainApp;
    

    public Line(App mainApp){
        this.mainApp = mainApp;
    }



    public boolean check_mouseOver(int mosx, int mosy){
        for(int i = 0; i < segments.size(); i++){

            double distance1 = Math.sqrt(Math.pow((mosx - segments.get(i).x1),2) + Math.pow((mosy - segments.get(i).y1),2));
            double distance2 = Math.sqrt(Math.pow((mosx - segments.get(i).x2),2) + Math.pow((mosy - segments.get(i).y2),2));


            if(distance1 < 10 || distance2 < 10){
                return true;
            }
            
        }
        return false;
    }

    public CollisionReport checkCollision(){
        for(int j = 0; j< mainApp.balls.size(); j++){
            for(int i = 0; i < segments.size(); i++){

                double distance1 = Math.sqrt(Math.pow((mainApp.balls.get(j).centreX - segments.get(i).x1),2) + Math.pow((mainApp.balls.get(j).centreY - segments.get(i).y1),2));
                double distance2 = Math.sqrt(Math.pow((mainApp.balls.get(j).centreX - segments.get(i).x2),2) + Math.pow((mainApp.balls.get(j).centreY - segments.get(i).y2),2));
    
    
                if(distance1 < 16 || distance2 < 16){

                    //Line segment x and y lengths
                    //The x and y are flipped, dont ask why this works, but it does otherwise it screws up
                    double dy = segments.get(i).x2 -segments.get(i).x1;
                    double dx = segments.get(i).y2 -segments.get(i).y1;


                    double magnitude = Math.sqrt(dx*dx + dy*dy);

                   

                    //Avoid division by zero and assign a dx value
                    if(magnitude == 0){
                        dx =1;
                        dy= 0;
                        magnitude = 1;
                    }
                    
                    
                    //Pair of normals, only one shall be used
                    double n1y =(-dy)/(magnitude);
                    double n1x =(dx)/(magnitude);

                    double n2y =(dy)/(magnitude);
                    double n2x =(-dx)/(magnitude);

                    //Chose NORMALS = (-dy,dx),  (dy,-dx)
                    double nx = 0;
                    double ny = 0;

                    double midx = (segments.get(i).x2 + segments.get(i).x1)/2;
                    double midy = (segments.get(i).y2 + segments.get(i).y1)/2;

                    double n1posx = midx + n1x;
                    double n1posy = midy + n1y;
                    double n2posx = midx + n2x;
                    double n2posy = midy + n2y;

                    

                    double n1Distance =  Math.sqrt(Math.pow((mainApp.balls.get(j).centreX - n1posx),2) + Math.pow((mainApp.balls.get(j).centreY - n1posy),2));
                    double n2Distance =  Math.sqrt(Math.pow((mainApp.balls.get(j).centreX - n2posx),2) + Math.pow((mainApp.balls.get(j).centreY - n2posy),2));

                    

                    if(n1Distance < n2Distance){
                        nx = n1x;
                        ny = n1y;
                    }
                    if(n2Distance <= n1Distance){
                        nx = n2x;
                        ny = n2y;
                    }

                    /*DEBUG NORMAL POINTERS
                    mainApp.setn1(new LinePoint((float)n1posx,(float)n1posx,(float)n1posy, (float)n1posy));
                    mainApp.setn2(new LinePoint((float)n2posx,(float)n2posx,(float)n2posy,(float)n2posy));
                    */


                    //New velocity components

                    
                    double dotProduct = mainApp.balls.get(j).iVelocity * nx + mainApp.balls.get(j).jVelocity * ny;

                    double ux = mainApp.balls.get(j).iVelocity   -  2 * (dotProduct) * nx;
                    double uy = mainApp.balls.get(j).jVelocity   -  2 * (dotProduct) * ny;

                   

                    return  new CollisionReport(true, ux, uy, mainApp.balls.get(j));

                }
                
            }
            
        }
        return new CollisionReport(false);

    }

    public void set_points(LinkedList<LinePoint> linePoints){
        this.segments = linePoints;
    }

}



