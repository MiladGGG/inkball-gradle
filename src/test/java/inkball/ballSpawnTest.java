package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
public class ballSpawnTest {

    @Test
    public void testSpawn() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000); // delay is to give time to initialise stuff before drawing begin
        Ball ball = app.spawnerBall(app.ball0img,Colours.c.Grey);

        //Check if the latest ball is indeed the spawned ball
        assertEquals(ball, app.balls.get(app.balls.size() -1 ));
    }
    
}

