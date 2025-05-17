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

    boolean throttleOn = false;
    boolean slowDownOn = false;

    int tilesPoisonedBehindLimit = 150;
    int tickCount = 0;

    Color headColor;
    Color bodyColor;

    String name;
    Pose2d pose;

    /**
     * Lovely lovely constructor
     * @param X X array
     * @param Y Y array
     * @param headColor Head color
     * @param bodyColor Body Color
     */
    public LightCycle(String name, int[] X, int[] Y, Color headColor, Color bodyColor, Pose2d initialPose) {
        this.name = name;
        this.X = X;
        this.Y = Y;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        pose = initialPose;
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
    public void updateMovement() {
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
                Y[0]=Y[0]- GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.DOWN:
                Y[0]=Y[0]+ GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.LEFT:
                X[0]=X[0]- GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.RIGHT:
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
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(pose.direction != Direction.RIGHT){
                        pose.direction = Direction.LEFT;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(pose.direction != Direction.LEFT){
                        pose.direction = Direction.RIGHT;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(pose.direction != Direction.DOWN){
                        pose.direction = Direction.UP;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(pose.direction != Direction.UP){
                        pose.direction = Direction.DOWN;
                    }
                    break;
            }
        }
    }

    /**
     * Handles bike throttle/slowdown toggles, may need to be reworked
     */
    public class BikeKeyThrottleMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SHIFT:
                    throttleOn = true;
                    break;
                case KeyEvent.VK_CONTROL:
                    slowDownOn = true;
                    break;
                default:
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




