package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
public class ballCaptureTest {

    @Test
    public void testCapture() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000); // delay is to give time to initialise stuff before drawing begin
        
        //Create a mock grey hole
        Tile hole = new Tile(0,0,app);
        hole.isHole = true;
        hole.colour = Colours.c.Grey;

        //Get a ball
        Ball ball = app.balls.get(0);

        int previous_count = app.balls.size();

        app.ballHole(ball, hole);

        int following_count = app.balls.size();

        //Score has properly increased
        assertEquals(70, app.score);

        //The ball is gone and captured
        assertEquals(previous_count -1, following_count);
    }
    
}

