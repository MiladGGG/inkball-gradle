package inkball;

import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("unused")
public class lossConditionTest {

    @Test
    public void testloss() {
        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        app.delay(1000); // delay is to give time to initialise stuff before drawing begin
        
        //Set time to zero
        app.time = 0;
        //Update time
        app.drawTime();


        //Has the game been lost?
        assertEquals(true, app.isEnd);
    }
    
}

