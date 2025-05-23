import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LightCycle {

    public static class Pose2d {
        public int x;
        public int y;
        public Direction direction;

        Pose2d(int xInitial, int yInitial, Direction directionInitial) {
            x = xInitial;
            y = yInitial;
            direction = directionInitial;
        }
    }

    private final int[] X;
    private final int[] Y;

    int[] keyMap;

    boolean throttleOn = false;
    boolean slowDownOn = false;

    int tilesPoisonedBehindLimit = 150;
    int tickCount = 0;

    Color headColor;
    Color bodyColor;

    String name;
    public Pose2d pose;

    /**
     * Lovely lovely constructor
     * @param X X array
     * @param Y Y array
     * @param headColor Head color
     * @param bodyColor Body Color
     */
    public LightCycle(String name, int[] X, int[] Y, Color headColor, Color bodyColor, Pose2d initialPose, int[] keyMap) {
        this.name = name;
        this.X = X;
        this.Y = Y;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        pose = initialPose;
        this.keyMap = keyMap;
    }

    /**
     * Reflect backend changes onto the Swing app
     * @param g Graphics renderer
     */
    public void draw(Graphics g) {
        for(int i = 0; i< tilesPoisonedBehindLimit; i++){
            if(i==0){
                g.setColor(headColor);             //for the head
            }else {
                g.setColor(bodyColor); //for the body

            }
            g.fillRect(X[i],Y[i], GamePanel.Constants.UNIT_SIZE, GamePanel.Constants.UNIT_SIZE);
        }
    }

    /**
     * Pre-fill the trail and head position.
     * @param startX pixel X
     * @param startY pixel Y
     */
    public void initPosition(int startX, int startY) {
        for (int i = 0; i < tilesPoisonedBehindLimit; i++) {
            X[i] = startX;
            Y[i] = startY;
        }
        // also keep your logical grid pose in sync:
        pose.x = startX / GamePanel.Constants.UNIT_SIZE;
        pose.y = startY / GamePanel.Constants.UNIT_SIZE;
    }

    /**
     * Checks if a lightcycle has collided with something
     * @return true if the lightcycle has collided with something, false if not
     */
    public boolean runLightcycleCollisionCheck() {
        //checks for collisions with trail
        for(int i = tilesPoisonedBehindLimit; i>0; i--){
            if((X[0]== X[i]) && (Y[0]== Y[i])){
                return true;
            }
        }

        //just let the if chain be, because why not :(

        //check if head touches left border
        if(X[0] < 0){
            return true;
        }
        //checks if head touches right border
        else if(X[0] > GamePanel.Constants.SCREEN_WIDTH){
            return true;
        }
        //checks if head touched top border
        else if(Y[0] < 0){
            return true;
        }
        //checks if head touched bottom border
        else if(Y[0] > GamePanel.Constants.SCREEN_HEIGHT){
            return true;
        }

        return false;
    }

    /**
     * Decides how often to actually move the lightcycle
     */
    public void updateMovement() { //TODO: Fine tune speed up and slow down speed rates
        tickCount++;
        if (throttleOn) { //acceleration, move 2 per tick
            move();
            move();
        } else if (slowDownOn) {
            if (tickCount % 2 == 0) {
                move(); //deceleration, move once per 2 ticks
            }
        } else {
            move(); //normal speed, move once per tick
        }
    }

    /**
     * Responsible for modifying backend direction changes to the coordinate arrays
     */
    public void move(){
        for(int i = tilesPoisonedBehindLimit; i>0; i--){
            X[i] = X[i-1];
            Y[i] = Y[i-1];
        }

        switch (pose.direction){
            case Direction.UP:
                pose.y++;
                Y[0]=Y[0]- GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.DOWN:
                pose.y--;
                Y[0]=Y[0]+ GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.LEFT:
                pose.x--;
                X[0]=X[0]- GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.RIGHT:
                pose.x++;
                X[0]=X[0]+ GamePanel.Constants.UNIT_SIZE;
                break;
        }
    }

    /**
     * Handles bike directions
     */
    public class BikeKeyDirectionMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            if (keyCode == keyMap[2]) {
                if(pose.direction != Direction.RIGHT){
                    pose.direction = Direction.LEFT;
                }
            }
            else if (keyCode == keyMap[3]) {
                if(pose.direction != Direction.LEFT){
                    pose.direction = Direction.RIGHT;
                }
            }
            else if (keyCode == keyMap[0]) {
                if(pose.direction != Direction.DOWN){
                    pose.direction = Direction.UP;
                }
            }
            else if (keyCode == keyMap[1]) {
                if(pose.direction != Direction.UP){
                    pose.direction = Direction.DOWN;
                }
            }
        }
    }

    /**
     * Handles bike throttle/slowdown toggles, may need to be reworked
     */
    public class BikeKeyThrottleMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == keyMap[4]) {
                throttleOn = true;
            }
            else if (keyCode == keyMap[5]) {
                slowDownOn = true;
            }
            else {
                throttleOn = false;
                slowDownOn = false;
            }
        }
    }

    //getters.. getting they stuffs
    public KeyAdapter getBikeKeyDirectionMonitor() {return new BikeKeyDirectionMonitor();}
    public KeyAdapter getBikeKeyThrottleMonitor() {return new BikeKeyThrottleMonitor();}

    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}




