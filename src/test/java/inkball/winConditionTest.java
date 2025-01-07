package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
public class winConditionTest {

    @Test
    public void testWin() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000); // delay is to give time to initialise stuff before drawing begin
        
        //Clear all balls
        app.balls.clear();
  
        //Clear the queue
        app.GUIball.clear();
        
        //Draw
        app.draw();


        //Check if game is won
        assertEquals(true, app.isWon);
    }
    
}

