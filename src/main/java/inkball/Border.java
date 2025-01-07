package inkball;

public class Border implements Collision{

    LinePoint left = new LinePoint(0,0,0,640);
    LinePoint right = new LinePoint(576,576,0,640);
    LinePoint up = new LinePoint(0,576,0,0);
    LinePoint down = new LinePoint(0,576,640,640);


    LinePoint[] walls = {left, right, up , down};

    App mainApp;

    public Border(App mainApp){
        this.mainApp = mainApp;
    }




    public CollisionReport checkCollision(){
        for(int j = 0; j< mainApp.balls.size(); j++){
            Ball ball = mainApp.balls.get(j);



            //TOP
            if(ball.centreY - ball.jVelocity  < 0 + ball.radius + App.TOPBAR){
                return new CollisionReport(true,ball.iVelocity, ball.jVelocity * -1, ball);
            }
            //BOTTOM
            if(ball.centreY - ball.jVelocity  > 640 - ball.radius){
                return new CollisionReport(true,ball.iVelocity, ball.jVelocity * -1, ball);
            }
            //LEFT
            if(ball.centreX - ball.iVelocity  < 0 + ball.radius){
                return new CollisionReport(true,ball.iVelocity *-1, ball.jVelocity, ball);
            }
            //RIGHT
            if(ball.centreX - ball.iVelocity  > 540 +  ball.radius ){
                return new CollisionReport(true,ball.iVelocity *-1, ball.jVelocity, ball);
            }
        }

        return new CollisionReport(false);

    }
}

