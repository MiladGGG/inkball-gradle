package inkball;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.checkerframework.checker.units.qual.Time;


@SuppressWarnings("unused")
public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;

    public static Random random = new Random();



    //LEVEL ATTRIBUTES FROM JSON FILE
    public String layout;
    public char[] charLayout;
    public int time;
    public int spawn_interval;
    public double score_increase;
    public double score_decrease;
    public String[] balls_colour;

    public int[] capture_increase = new int[5];
    public int[] capture_decrease = new int[5];
    

    //TILES & SPRITES
    public Tile[] tiles = new Tile[324];

    PImage tileimg;
    PImage wall0img;
    PImage wall1img;
    PImage wall2img;
    PImage wall3img;
    PImage wall4img;
    PImage spawnerimg;
    PImage hole0img;
    PImage hole1img;
    PImage hole2img;
    PImage hole3img;
    PImage hole4img;
    
    PImage ball0img;
    PImage ball1img;
    PImage ball2img;
    PImage ball3img;
    PImage ball4img;

    public PImage[] ballCols = new PImage[5];

    //BALLS
    public ArrayList<Ball> balls = new ArrayList<Ball>();




    //SPAWNER
	public ArrayList<Tile> spawners = new ArrayList<Tile>();
    float spawn_time_left;
    float spawn_offset;
    int spawned_balls; 
    float spawn_paused_time;
    boolean initialball;
    ArrayList<Colours.c> queuedColours = new ArrayList<Colours.c>();
    ArrayList<PImage> queuedSprites = new ArrayList<PImage>();
    int add;

    //GUI
    public ArrayList<BallGUI> GUIball = new ArrayList<BallGUI>();

    //BORDER
    Border border = new Border(this);

    //LINES LOGIC
    public ArrayList<Line> player_lines = new ArrayList<Line>();
    public LinkedList<LinePoint> linePoints = new LinkedList<LinePoint>();
    boolean clickFlag;
    boolean rClickFlag;
    boolean ctrlFlag;
    int mosX1;
    int mosY1;
    int mosX2;
    int mosY2;
    public Line current_line;

    public LinePoint n1;
    public LinePoint n2;
    



    //GAME LOGIC
    public int score;
    public boolean ispaused;
    public boolean isEnd;
    int remaining_time;
    int elapsed_time;
    int saved_time;
    int time_reset;
    int paused_time;

    //Level switch
    int level_int = 0;
    int frame_count;
    boolean isWon;
    int scoring_time;
    boolean switchFlag;

    int tileProgress;
    int tileDir;
    PImage lastSprite;

    int tileProgress2;
    int tileDir2;
    PImage lastSprite2;

    boolean second_once;





    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
		
        JSONObject obj = loadJSONObject(configPath);
        JSONArray lvls = (JSONArray)obj.get("levels");

        //Increase
        JSONObject inc = (JSONObject)obj.get("score_increase_from_hole_capture");

        capture_increase[0] = (int)inc.get("grey");
        capture_increase[1] = (int)inc.get("orange");
        capture_increase[2] = (int)inc.get("blue");
        capture_increase[3] = (int)inc.get("green");
        capture_increase[4] = (int)inc.get("yellow");

        //Decrease
        JSONObject dec = (JSONObject)obj.get("score_decrease_from_wrong_hole");

        capture_decrease[0] = (int)dec.get("grey");
        capture_decrease[1] = (int)dec.get("orange");
        capture_decrease[2] = (int)dec.get("blue");
        capture_decrease[3] = (int)dec.get("green");
        capture_decrease[4] = (int)dec.get("yellow");


        //Selected Level
        JSONObject lvl = lvls.getJSONObject(level_int);

        //LOAD VALUES FROM JSON FILE
        layout = (String)lvl.get("layout");
        try{
            File f = new File(layout);
            Scanner scan = new Scanner(f);

            String lvl_tmp = "";
            while(scan.hasNextLine()){
                lvl_tmp += scan.nextLine();
            }
            layout = lvl_tmp;
            scan.close();
        }
        catch(Exception e){
            System.out.println(e);
            System.out.println("\n\n\n ERROR IN LEVEL FILE HANDLING STUPID BOY\n\n\n\n");
        }
        charLayout = layout.toCharArray();


        time = (int)lvl.get("time");
        spawn_interval = (int)lvl.get("spawn_interval");
        score_increase = (double)lvl.get("score_increase_from_hole_capture_modifier");
        score_decrease = (double)lvl.get("score_decrease_from_wrong_hole_modifier");
        JSONArray ballstemp = (JSONArray)lvl.get("balls");
        balls_colour = new String[ballstemp.size()];
        for(int i = 0; i < ballstemp.size(); i++){
            balls_colour[i] = (String)ballstemp.get(i);
        }

        initialball = true;
        

        
        //LOAD SPRITES 
        tileimg = loadImage("src/main/resources/inkball/tile.png");
        wall0img = loadImage("src/main/resources/inkball/wall0.png");
        wall1img = loadImage("src/main/resources/inkball/wall1.png");
        wall2img = loadImage("src/main/resources/inkball/wall2.png");
        wall3img = loadImage("src/main/resources/inkball/wall3.png");
        wall4img = loadImage("src/main/resources/inkball/wall4.png");
        spawnerimg = loadImage("src/main/resources/inkball/entrypoint.png");
        hole0img = loadImage("src/main/resources/inkball/hole0.png");
        hole1img = loadImage("src/main/resources/inkball/hole1.png");
        hole2img = loadImage("src/main/resources/inkball/hole2.png");
        hole3img = loadImage("src/main/resources/inkball/hole3.png");
        hole4img = loadImage("src/main/resources/inkball/hole4.png");

        ball0img = loadImage("src/main/resources/inkball/ball0.png");
        ball1img = loadImage("src/main/resources/inkball/ball1.png");
        ball2img = loadImage("src/main/resources/inkball/ball2.png");
        ball3img = loadImage("src/main/resources/inkball/ball3.png");
        ball4img = loadImage("src/main/resources/inkball/ball4.png");

        ballCols[0] = ball0img;
        ballCols[1] = ball1img;
        ballCols[2] = ball2img;
        ballCols[3] = ball3img;
        ballCols[4] = ball4img;



        //SPAWNER BALLS
        // queuedColours = new Colours.c[balls_colour.length];
        for(int i = 0; i < balls_colour.length; i++){
            queuedColours.add(Colours.getColour(balls_colour[i])); 

        }
        // queuedSprites = new PImage[balls_colour.length];
        for(int i = 0; i < balls_colour.length; i++){
            queuedSprites.add(ballCols[Colours.getSpriteIndex(queuedColours.get(i))]); 
        }
        // spawn_interval = 1;

        int maxtmp = balls_colour.length;
        if (maxtmp>5)
        maxtmp = 5;

        for(int i = 0; i < maxtmp; i++){
            GUIball.add(new BallGUI(20 +i* 35 , 17, queuedSprites.get(i),this));
        }

        //TILES SETUP
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                this.tiles[j + (i*18)] = new Tile(CELLSIZE*(j),TOPBAR + (i *CELLHEIGHT), this);
            }
        }
        for(int i = 0; i < 324; i++){
            tiles[i].arrayInd = i;
        }
        //TILE
        for(int i = 0; i < 324; i++){
            switch (charLayout[i]) {
                case ' ':
                    tiles[i].setSprite(tileimg);
                    break; 
                case 'X':
                    tiles[i].setSprite(wall0img);
                    // if(tiles[i].arrayInd == 18*13 )
                    tiles[i].collisionEnabled = true;
                    break;
                case '1':
                    tiles[i].setSprite(wall1img);
                    tiles[i].colour = Colours.c.Orange;
                    tiles[i].collisionEnabled = true;
                    break;
                case '2':
                    tiles[i].setSprite(wall2img);
                    tiles[i].colour = Colours.c.Blue;
                    tiles[i].collisionEnabled = true;
                    break;  
                case '3':
                    tiles[i].setSprite(wall3img);
                    tiles[i].colour = Colours.c.Green;
                    tiles[i].collisionEnabled = true;
                    break; 
                case '4':
                    tiles[i].setSprite(wall4img);
                    tiles[i].colour = Colours.c.Yellow;
                    tiles[i].collisionEnabled = true;
                    break;        
                case 'H':
                    tiles[i].isHole = true;
                    if(charLayout[i + 1] == '0'){
                        tiles[i].sprite = hole0img;
                        tiles[i].colour = Colours.c.Grey;
                    } 
                    if(charLayout[i + 1] == '1'){
                        tiles[i].sprite = hole1img;
                        tiles[i].colour = Colours.c.Orange;
                    }
                    if(charLayout[i + 1] == '2'){
                        tiles[i].sprite = hole2img;
                        tiles[i].colour = Colours.c.Blue;
                    }
                    if(charLayout[i + 1] == '3'){
                        tiles[i].sprite = hole3img;
                        tiles[i].colour = Colours.c.Green;
                    }       
                    if(charLayout[i + 1] == '4'){
                        tiles[i].sprite = hole4img;
                        tiles[i].colour = Colours.c.Yellow;
                    }

                    tiles[i+ 1].banish();
                    tiles[i+ 0 + 18].banish();
                    tiles[i+ 1+ 18].banish();
                    break;
                
                case 'S':
                    tiles[i].setSprite(spawnerimg);
                    tiles[i].isSpawner = true;
                    spawners.add(tiles[i]);
                    break; 
                case 'B':
                    tiles[i].setSprite(tileimg);
                    tiles[i+1].ball_wall(tileimg);

                    PImage ballsprite = null;
                    Colours.c colour = Colours.c.Grey;
                    if(charLayout[i + 1] == '0'){
                        ballsprite = ball0img;
                        colour = Colours.c.Grey;
                    } 
                    if(charLayout[i + 1] == '1'){
                        ballsprite = ball1img;
                        colour = Colours.c.Orange;
                    }
                    if(charLayout[i + 1] == '2'){
                        ballsprite = ball2img;
                        colour = Colours.c.Blue;
                    }
                    if(charLayout[i + 1] == '3'){
                        ballsprite = ball3img;
                        colour = Colours.c.Green;
                    }       
                    if(charLayout[i + 1] == '4'){
                        ballsprite = ball3img;   
                        colour = Colours.c.Yellow;
                    }
                    Ball b = spawnBall(ballsprite, tiles[i].getX(),tiles[i].getY());
                    b.colour = colour;
                    break; 
                default:
                    break;
            }
        }

    }

    Ball spawnBall(PImage sprite, float x, float y){
        Ball ball = new Ball(x,y, this);
        ball.setSprite(sprite);
        balls.add(ball);

        return ball;
    }
    
    Tile getRandomSpawner(){
        Random rand = new Random();
        
        return spawners.get(rand.nextInt(spawners.size()));
    }


    public Ball spawnerBall(PImage sprite, Colours.c colour){
        Tile spawner = getRandomSpawner();

        float x = spawner.x +16;
        float y = spawner.y +16;

        Ball ball = new Ball(x,y, this);
        ball.setSprite(sprite);
        ball.colour = colour;
        balls.add(ball);

        GUIball.remove(GUIball.get(0));
        if(GUIball.size() >0){
            
            GUIball.get(0).isNext = true;
        }

        if(spawned_balls +5 < balls_colour.length)
        GUIball.add(new BallGUI(20 +5* 35 , 17, queuedSprites.get(spawned_balls +5),this));

        if(spawn_time_left == 0){
            spawn_time_left = time;
        }

        return ball;
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if(event.getKey() == 'r'){
            reset(0);

        }
        if(event.getKey() == ' '){
            if(ispaused == false){
                ispaused = true;
                

            }
            else{
                ispaused = false;
                frameCount = 0;
            }

        }
        
        if(event.getKey() == 'g'){
            GUIball.add(new BallGUI(20 +GUIball.size()* 35 + GUIball.get(0).offset , 17, ball4img, this));

        }
    

        if(event.getKeyCode() == CONTROL){
            ctrlFlag = true;
        }
    }


    void reset(int new_score){
        isEnd = false;
        isWon = false;
        time_reset += elapsed_time;
        balls.clear();
        spawners.clear();
        player_lines.clear();
        linePoints.clear();
        elapsed_time = millis() - time_reset - paused_time;
        spawned_balls = 0;
        spawn_offset = 0;
        spawn_paused_time = 0;
        initialball = true;
        GUIball.clear();
        queuedColours.clear();
        queuedSprites.clear();

        tileProgress = 0;
        tileDir = 0;
        lastSprite = null;
    
        tileProgress2 =0 ;
        tileDir2= 0;
        lastSprite2 = null;
    
        second_once = false;

        score = new_score;

        setup();
    }
    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(KeyEvent event){
        if(event.getKeyCode() == CONTROL){
            ctrlFlag = false;
        }  
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(isEnd == false){


        if(e.getButton() == LEFT && ctrlFlag == false){ 
        
            clickFlag = true;
            mosX1 = e.getX();
            mosY1 = e.getY();

            linePoints.add(new LinePoint(mosX1, mosX1, mosY1, mosY1));

            current_line = new Line(this);
            player_lines.add(current_line);
            current_line.set_points(linePoints);
            }
        

        if(e.getButton() == RIGHT || (e.getButton() == LEFT && ctrlFlag)){


            rClickFlag = true;
        }

        }
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
        


       

        if(clickFlag == true){

            mosX2 = e.getX();
            mosY2 = e.getY();

            
            linePoints.add(new LinePoint(mosX1, mosX2, mosY1, mosY2));
            mosX1 = mosX2;
            mosY1 = mosY2;

            current_line.set_points(linePoints);
        }
        if(rClickFlag == true){
            for(int i = 0; i < player_lines.size(); i++){
                
                boolean check = player_lines.get(i).check_mouseOver(e.getX(), e.getY());
                if(check == true){
                    player_lines.remove(i);
                }
            }
        }
    
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if(isEnd == false){

  
       
        if(e.getButton() == LEFT){ 
            clickFlag = false;
            linePoints = new LinkedList<LinePoint>();
            current_line = null;
            }

        if(e.getButton() == RIGHT){
            rClickFlag = false;
        }
    }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
    background(200, 200, 200);


    //Draw tiles
    for(int i = 0; i < 324; i++){
        if(tiles[i].sprite != null)
        tiles[i].draw();
    }

    //Draw balls
    for(int i = 0; i < balls.size(); i++){
        if(balls.get(i).sprite != null)
        balls.get(i).draw();
    }



    
    //Border Collision
    CollisionReport borderCollisionReport = border.checkCollision();  
    if(borderCollisionReport.collision_occured){
        borderCollisionReport.ball.iVelocity = borderCollisionReport.ux;
        borderCollisionReport.ball.jVelocity = borderCollisionReport.uy;
    }

    //Draw Lines
    stroke(0,0,0);
    strokeWeight(10);
    for(int i = 0; i < player_lines.size(); i++){

        for(int j = 0; j< player_lines.get(i).segments.size(); j++){
            line(player_lines.get(i).segments.get(j).x1,player_lines.get(i).segments.get(j).y1,player_lines.get(i).segments.get(j).x2,player_lines.get(i).segments.get(j).y2);
        }

        //Line collision
        CollisionReport lineCollisionReport = player_lines.get(i).checkCollision();  
        if(lineCollisionReport.collision_occured){
            player_lines.remove(i);
            
            lineCollisionReport.ball.iVelocity = lineCollisionReport.ux;
            lineCollisionReport.ball.jVelocity = lineCollisionReport.uy;


            //DeBUG 
            // lineCollisionReport.ball.iVelocity = 0;
            // lineCollisionReport.ball.jVelocity = 0;

        }

    }

    //DEBUG POINTERS
    strokeWeight(5);
    stroke(255,2,2);
    if(n1 != null){
        line(n1.x1,n1.y1,n1.x2,n1.y2);
    }
    stroke(2,255,2);
    if(n2 != null){
        line(n2.x1,n2.y1,n2.x2,n2.y2);
    }
    
    //DETECT WIN
    if(balls.size() == 0 && GUIball.size() == 0 && isWon == false){
        isEnd = true;
        isWon = true;
        scoring_time = remaining_time;
        frameCount =1;
        
    }
    
    //GAME END
    if(isWon && scoring_time != 0){
        if(ispaused == false){
            if(frameCount == 1){
                scoring_time -= 1;
                score += 1;
                frameCount = 0;
    
                if(lastSprite == null)
                lastSprite = tiles[0].sprite;
                tileDance();
            }
            else{
                frameCount++;
            }   
        }
 
    }

    if(isWon && scoring_time == 0){
        if(level_int + 1 < 3)
        level_int++;
        reset(score);
    }
        //UI elements
        drawScore();
        drawQueue();
        drawSpawnTime();
        drawTime();
    }




    void tileDance(){
        tiles[tileProgress].setSprite(lastSprite);

        if(tileProgress == 17 && tileDir == 0){
            tileDir++;
        }
        if(tileProgress ==323 && tileDir ==1){
            tileDir++;

        }
        if(tileProgress ==323- 17 && tileDir == 2){
            tileDir++;

        }
        if(tileProgress == 0 && tileDir == 3){
            tileDir = 0;
            tileProgress = 0;
        }


        


        if(tileDir == 0){
            tileProgress++;
        }
        if(tileDir ==1 ){
            tileProgress +=18;
        }
        if(tileDir ==2 ){
            tileProgress--;
        }
        if(tileDir ==3 ){
            tileProgress-= 18;
        }


        lastSprite = tiles[tileProgress].sprite;
        tiles[tileProgress].setSprite(wall4img);
        
        if(second_once == false){
            second_once = true;
            tileDir2 = 2;
            tileProgress2 =323;
            lastSprite2 = tiles[323].sprite;
        }
        tileDance2();
    }



    void tileDance2(){
        tiles[tileProgress2].setSprite(lastSprite2);

        if(tileProgress2 == 17 && tileDir2 == 0){
            tileDir2++;
        }
        if(tileProgress2 ==323 && tileDir2 ==1){
            tileDir2++;

        }
        if(tileProgress2 ==323- 17 && tileDir2 == 2){
            tileDir2++;

        }
        if(tileProgress2 == 0 && tileDir2 == 3){
            tileDir2 = 0;
            tileProgress2 = 0;
        }


        


        if(tileDir2 == 0){
            tileProgress2++;
        }
        if(tileDir2 ==1 ){
            tileProgress2+=18;
        }
        if(tileDir2 ==2 ){
            tileProgress2--;
        }
        if(tileDir2 ==3 ){
            tileProgress2-= 18;
        }


        lastSprite2 = tiles[tileProgress2].sprite;
        tiles[tileProgress2].setSprite(wall4img);
    }



public void drawScore(){
    PFont f = createFont("Arial",CELLSIZE*0.8f,true);
    textFont(f,CELLSIZE * 0.8f);           // STEP 3 Specify font to be used
    fill(25,25,25);
    textAlign(CENTER, CENTER);

    String scoreText = "Score: "+ score;
    text( scoreText,WIDTH- WIDTH/6 + 30,TOPBAR/2 -10);

}

public void drawTime(){


        if(ispaused == false && isEnd == false)
        elapsed_time = millis() - time_reset - paused_time;

        remaining_time = time-elapsed_time/1000;
        if(remaining_time <= 0){
            isEnd = true;
            ispaused = false;
            clickFlag = false;
            rClickFlag = false;


            //Header
            fill(255,190,190);
            stroke(255,0,0);
            rect(0,0 ,576,64);


            PFont a = createFont("Arial",CELLSIZE*1.5f,true);
            textFont(a,CELLSIZE * 1.5f);           // STEP 3 Specify font to be used
            fill(200,25,25);
            textAlign(CENTER, CENTER);
            text( "=== TIME'S UP ===",WIDTH- WIDTH/2 - 50,TOPBAR/2);



        }

        if(ispaused){
            PFont f = createFont("Arial",CELLSIZE*0.8f,true);
            textFont(f,CELLSIZE * 0.8f);           // STEP 3 Specify font to be used
            fill(25,25,25);
            textAlign(CENTER, CENTER);
            text( "**PAUSED**",WIDTH- WIDTH/6 -150,TOPBAR/2 -10 +25);

        }
        if(ispaused || isEnd){
            paused_time = millis() - time_reset - elapsed_time;
        }
        PFont f = createFont("Arial",CELLSIZE*0.8f,true);
        textFont(f,CELLSIZE * 0.8f);           // STEP 3 Specify font to be used
        fill(25,25,25);
        textAlign(CENTER, CENTER);
        if(5 == 10){
            if(saved_time == 0){
            saved_time = elapsed_time;
            }
            text( "Time: "+saved_time/1000,WIDTH- WIDTH/8,TOPBAR/2);

        }
        else{
            if(isWon == false)
            text( "Time: "+remaining_time,WIDTH- WIDTH/6 + 30,TOPBAR/2 -10 +25);
            if(isWon)
            text( "Time: "+scoring_time,WIDTH- WIDTH/6 + 30,TOPBAR/2 -10 +25);

        }
    }


    public void drawSpawnTime(){


        float spawn_time = millis() - time_reset - paused_time;


        spawn_time_left = spawn_interval - spawn_time/1000 + spawn_offset + spawn_paused_time;
        //ROUND to 1 dp
        spawn_time_left = (float)(Math.round(spawn_time_left*Math.pow(10,1))/Math.pow(10,1));

        if(initialball && ispaused == false){spawn_time_left = 0; initialball =false; spawn_offset -= spawn_interval;}

        if(spawn_time_left <= 0){
            if(spawned_balls < queuedColours.size()){

                int arrayNum = spawned_balls;
                spawnerBall(queuedSprites.get(arrayNum), queuedColours.get(arrayNum));
                
                spawn_paused_time = 0;
                spawned_balls += 1;
                spawn_offset += spawn_interval;
                

            }

        }
        if(GUIball.size() == 0){

                float remainder  =  (float)remaining_time/10;
                float leftover = remainder - floor(remainder);
    
    
                spawn_paused_time  = spawn_interval- leftover * spawn_interval;
            
            

           
        }
        String timeString = Float.toString(spawn_time_left);

        PFont f = createFont("Arial" ,CELLSIZE*0.8f,true);
        textFont(f,CELLSIZE * 0.8f);           // STEP 3 Specify font to be used
        fill(25,25,25);
        textAlign(CENTER, CENTER);
        if(spawned_balls >= queuedColours.size())
        timeString = "0.0";
        text(timeString,WIDTH- 2*WIDTH/3 + 30,TOPBAR/2 +10);

    }

    public void drawQueue(){
        fill(0,0,0);
        stroke(0,0,0);
        rect(20,12 ,32 * 5 +10,32 + 8);



        for(int i = 0; i <GUIball.size(); i++){
            GUIball.get(i).draw();
        }


        //COVER
        fill(200,200,200);
        stroke(200,200,200);
        rect(35 + 32 * 5 ,12 ,40,32 + 8);
    }




    public void ballHole(Ball ball, Tile hole){
        if(ball.colour == hole.colour){
            score += capture_increase[Colours.getSpriteIndex(hole.colour)] *score_increase;
        }
        else if(ball.colour == Colours.c.Grey || hole.colour == Colours.c.Grey){
            score += capture_increase[0] *score_increase;
        }
        else{
            score -= capture_decrease[Colours.getSpriteIndex(hole.colour)] *score_decrease;

            if(GUIball.size() ==0)
            spawn_offset += spawn_interval;

            queuedSprites.add(ball.sprite);
            queuedColours.add(ball.colour);
            if(GUIball.size() < 5 && GUIball.size() > 0)
            GUIball.add(new BallGUI(20 +GUIball.size()* 35 + GUIball.get(0).offset , 17, ball.sprite,this));
    
            if(GUIball.size() < 5 && GUIball.size() == 0)
            GUIball.add(new BallGUI(20 +GUIball.size()* 35, 17, ball.sprite,this));

            // System.out.println("Added + " + GUIball.get(GUIball.size()-1).sprite);
        }



        balls.remove(ball);
    }

    public void setn1(LinePoint p){
        n1 = p;
    }
    public void setn2(LinePoint p){
        n2 = p;
    }
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}

