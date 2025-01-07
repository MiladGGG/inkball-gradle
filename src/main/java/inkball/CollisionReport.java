package inkball;

class CollisionReport{
    public boolean collision_occured;
    public double ux;
    public double uy;
    public Ball ball;

    public CollisionReport(boolean collision_occured, double ux, double uy, Ball ball){
        this.collision_occured = collision_occured;
        this.ux = ux;
        this.uy = uy;
        this.ball = ball;
    }

    public CollisionReport(boolean collision_occured){
        this.collision_occured = collision_occured;
    }
}
