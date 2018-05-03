import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.awt.Color;
import java.awt.*;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.File;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.*;
import java.applet.Applet.*;
import java.applet.*;
import java.io.*;

/**
 * Model of the game of breakout
 * @author Mike Smith University of Brighton
 */

public class Model extends Observable
{
    // Boarder
    private static final int B              = 6;  // Border offset
    private static final int M              = 40; // Menu offset
    private static int counter = 40;
    boolean alreadyExecute = false;
    boolean alreadyRun = false;
    int run = 0;

    boolean level1 = false;
    boolean level2 = false;

    // Size of things
    private static float BALL_SIZE    = 5; // Ball side
    private static final float BRICK_WIDTH  = 27; // Brick size
    private static final float BRICK_HEIGHT = 47;
    private static final float BAT_WIDTH = 150;
    private static final float BRICK_STR = 2;
    private Image ball1 = Toolkit.getDefaultToolkit().createImage("ball.png");

    private static final int BAT_MOVE       = 10; // Distance to move bat

    // Scores
    private static final int HIT_BRICK      = 50;  // Score
    private static final int HIT_BOTTOM     = -200;// Score

    private GameObj ball;          // The ball

    private List<GameObj> bricks;  // The bricks
    private List<GameObj> bricks2;  // The bricks
    private GameObj brickss;  // The bricks
    private GameObj bat;           // The bat

    private boolean runGame = true; // Game running
    private boolean fast = false;   // Sleep in run loop

    private int score = 0;

    private final float W;         // Width of area
    private final float H;         // Height of area

    public Model( int width, int height )
    {
        this.W = width; this.H = height;
    }

    /**
     * Create in the model the objects that form the game
     */

    public void createGameObjects()
    {
        synchronized( Model.class )
        {
            ball   = new GameObj(W/2, H/2, BALL_SIZE, BALL_SIZE,1, Colour.GREEN );

            bat    = new GameObj(210, H - BRICK_HEIGHT*1.5f, BAT_WIDTH, 
                BRICK_HEIGHT/4, 1, Colour.GRAY);
            bricks = new ArrayList<>();

            float pos = 50;
            float pos2 = 50;
            //first row
            for ( int i = 0; i < 10; i++){
                bricks.add(new GameObj(pos,300, BRICK_HEIGHT, BRICK_WIDTH, 2, Colour.BLUE));
                pos = pos + 50;
            }

            //Second row
            pos = 50;
            for ( int i = 0; i < 10; i++){
                bricks.add(new GameObj(pos,240, BRICK_HEIGHT, BRICK_WIDTH, 1, Colour.MAGENTA));
                pos = pos + 50;
            }
            //Third row
            pos = 50;
            for ( int i = 0; i < 10; i++){
                bricks.add(new GameObj(pos,270, BRICK_HEIGHT, BRICK_WIDTH, 1, Colour.MAGENTA));
                pos = pos + 50;
            }
            // Blue bricks for bricks to be hit twice


        }
    }

    private ActivePart active  = null;
    /**
     * Start the continuous updates to the game
     */
    public void startGame()
    {
        synchronized ( Model.class )
        {
            stopGame();
            active = new ActivePart();
            Thread t = new Thread( active::runAsSeparateThread );
            t.setDaemon(true);   // So may die when program exits
            t.start();
        }
    }

    /**
     * Stop the continuous updates to the game
     * Will freeze the game, and let the thread die.
     */
    public void stopGame()
    {  
        synchronized ( Model.class )
        {
            if ( active != null ) { active.stop(); active = null; }
        }
    }

    public GameObj getBat()             { return bat; }

    public GameObj getBall()            { return ball; }

    public List<GameObj> getBricks()    { return bricks; }

    /**
     * Add to score n units
     * @param n units to add to score
     */
    protected void addToScore(int n)    { score += n; }

    public int getScore()               { return score; }

    /**
     * Set speed of ball to be fast (true/ false)
     * @param fast Set to true if require fast moving ball
     */
    public void setFast(boolean fast)   
    { 
        this.fast = fast; 
    }

    /**
     * Move the bat. (-1) is left or (+1) is right
     * @param direction - The direction to move
     */
    public void moveBat( int direction )
    {
        float x = bat.getX();
        int pos1 = 0;
        int pos2 = 210 + 230;
        if (alreadyRun == true){
            if (x >= W - B - 250)  bat = new GameObj(110 + 230, H - BRICK_HEIGHT*1.5f, 250, 
                    BRICK_HEIGHT/4, 1, Colour.GRAY);
            if (x <= 0 + B            )  bat = new GameObj(0, H - BRICK_HEIGHT*1.5f, 250, 
                    BRICK_HEIGHT/4, 1, Colour.GRAY);
        } else if (alreadyRun == false){
            if (x >= W - B - BAT_WIDTH)  bat = new GameObj(210 + 230, H - BRICK_HEIGHT*1.5f, BAT_WIDTH, 
                    BRICK_HEIGHT/4, 1, Colour.GRAY);
            if (x <= 0 + B            )  bat = new GameObj(0, H - BRICK_HEIGHT*1.5f, BAT_WIDTH, 
                    BRICK_HEIGHT/4, 1, Colour.GRAY);}
        float dist = direction * BAT_MOVE; 
        
        bat.moveX(dist);
    }

    /**
     * This method is run in a separate thread
     * Consequence: Potential concurrent access to shared variables in the class
     */
    class ActivePart
    {
        private boolean runGame = true;

        public void stop()
        {
            runGame = false;
        }
        
        public void sound( ){
            breakSound.main();
        }

        public void runAsSeparateThread()
        {
            final float S = 3; // Units to move (Speed)
            Debug.trace("Welcome to BreakOut! Created by James Holliday");
            Debug.trace("The aim of the game is to destroy all the bricks on the screen by bouncing the ball into them using the paddle");
            Debug.trace("There are a total of 3 levels for you to beat");
            Debug.trace("At 1000, 2500 score points you unlock upgrades which will help you progress through the game");
            try
            {
                synchronized ( Model.class ) // Make thread safe
                {
                    GameObj       ball   = getBall();     // Ball in game

                    GameObj       bat    = getBat();      // Bat
                    List<GameObj> bricks = getBricks();   // Bricks

                }
                while (runGame)
                {
                    synchronized ( Model.class ) // Make thread safe
                    {
                        float x = ball.getX();  // Current x,y position
                        float y = ball.getY();
                        float by = bat.getY();
                        float bx = bat.getX();

                        // Deal with possible edge of board hit
                        if (x >= W - B - BALL_SIZE)  ball.changeDirectionX();
                        if (x <= 0 + B            )  ball.changeDirectionX();
                        if (y >= H - B - BALL_SIZE)  // Bottom
                        { 
                            ball.changeDirectionY(); addToScore( HIT_BOTTOM ); 
                        }
                        if (y <= 0 + M            )  ball.changeDirectionY();

                        // As only a hit on the bat/ball is detected it is 
                        //  assumed to be on the top or bottom of the object.
                        // A hit on the left or right of the object
                        //  has an interesting affect

                        for (int i = 0; i < bricks.size(); i++) {
                            if (ball.hitBy(bricks.get(i))) {
                                

                                if (bricks.get(i).getStrength() == 2){
                                    bricks.get(i).setStrength();
                                    bricks.get(i).setColour();
                                } else {
                                    bricks.remove(i);
                                }
                      
                                ball.changeDirectionY();
                                addToScore( HIT_BRICK  );
                                counter = counter - 1;
                                sound();
                            }
                        }

                        if ( score >= 1000 && alreadyExecute == false){
                            Debug.trace("Well done! You have unlocked a bigger ball for scoring 1000 points!");
                            ball = new GameObj(x, y, 15, 15,1, Colour.GREEN );
                            alreadyExecute = true;
                        } 

                        else if ( score >= 2500 && alreadyRun == false){
                            Debug.trace("Well done! You have unlocked a bigger paddle for scoring 2500 points!");
                            bat    = new GameObj(bx, by , 250, BRICK_HEIGHT/4, 1, Colour.GRAY);
                            alreadyRun = true;
                        }

                        if ( counter < 1 && level1 == false){
                            addToScore( HIT_BRICK  );
                            Debug.trace("Well done! You have completed Level 1!");
                            level1 = true;
                            int pos = 50;
                            for ( int i = 0; i < 10; i++){
                                bricks.add(new GameObj(pos,300, 46, 25, 1, Colour.MAGENTA));
                                pos = pos + 50;
                            }
                            pos = 50;
                            for ( int i = 0; i < 10; i++){
                                bricks.add(new GameObj(pos,242, 46, 25, 1, Colour.MAGENTA));
                                pos = pos + 50;
                            }
                            counter = 20;
                        }

                        if ( counter < 1 && level2 == false && level1 == true){
                            addToScore( HIT_BRICK  );
                            Debug.trace("Well done! You have completed Level 2!");
                            level2 = true;
                            int pos = 50;
                            for ( int i = 0; i < 5; i++){
                                bricks.add(new GameObj(pos,300, 46, 25, 1, Colour.MAGENTA));
                                pos = pos + 50;
                            }
                            pos = 100;
                            for ( int i = 0; i < 5; i++){
                                bricks.add(new GameObj(pos,240, 46, 25, 1, Colour.MAGENTA));
                                pos = pos + 50;
                            }
                            pos = 30;
                            for ( int i = 0; i < 10; i++){
                                bricks.add(new GameObj(pos,270, 46, 25, 1, Colour.MAGENTA));
                                pos = pos + 50;
                            }
                            counter = 20;
                        }

                        if ( counter < 1 && level2 == true && level1 == true){
                            Debug.trace("Well done! You have completed BreakOut!");
                            break;
                        }

                        if ( ball.hitBy(bat) )
                            ball.changeDirectionY();

                    }
                    modelChanged();      // Model changed refresh screen
                    Thread.sleep( fast ? 2 : 20 );
                    ball.moveX(S);  ball.moveY(S);

                }
            } catch (Exception e) 
            { 
                Debug.error("Model.runAsSeparateThread - Error\n%s", 
                    e.getMessage() );
            }
        }

        

        /**
         * Model has changed so notify observers so that they
         *  can redraw the current state of the game
         */
        public void modelChanged()
        {
            setChanged(); notifyObservers();}
    }
}

