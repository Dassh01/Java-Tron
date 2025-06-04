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

    public final int[] lightCycleX = new int[GamePanel.Constants.GAME_UNITS];
    public final int[] lightCycleY = new int[GamePanel.Constants.GAME_UNITS];

    int[] keyMap;

    boolean throttleOn = false;
    boolean slowDownOn = false;

    int trailLength = GamePanel.Constants.trailLength;
    int tickCount = 0;

    Color headColor;
    Color bodyColor;

    String name;
    private final Pose2d initialPose;

    //IMPORTANT: THIS POSE IS REFLECTIVE OF THE HEAD OF THE LIGHTCYCLE!! NOT THE TRAIL OR OTHER SEGMENTS!!
    public Pose2d lightCyclePose;

    Direction direction;
    /**
     * Lovely lovely constructor
     * @param headColor Head color
     * @param bodyColor Body Color
     */
    public LightCycle(String name, Color headColor, Color bodyColor, Pose2d initialPose, int[] keyMap) {
        this.name = name;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
        this.initialPose = initialPose;
        this.keyMap = keyMap;

        direction = initialPose.direction;

        lightCyclePose = new Pose2d(initialPose.x, initialPose.y, direction);
        initializeAtPose();
    }

    public void initializeAtPose() {
        lightCycleX[0] = initialPose.x;
        lightCycleY[0] = initialPose.y;
        direction = initialPose.direction;
        System.out.println("Pose of lightcycle " + name + " initialized at: ("+ lightCycleX[0] + "," + lightCycleY[0]+") \nFacing: " + direction);
    }

    public void updatePose() {
        lightCyclePose.x = lightCycleX[0];
        lightCyclePose.y = lightCycleY[0];
        lightCyclePose.direction = direction;
    }

    /**
     * Checks if a lightcycle has collided with a wall or itself NOT other lightcycles
     * @return true if the lightcycle has collided with something, false if not
     */
    public boolean runLightcycleLocalCollisionCheck() {
        //checks for collisions with trail
        for(int i = trailLength; i>0; i--){
            if((lightCycleX[0]== lightCycleX[i]) && (lightCycleY[0]== lightCycleY[i])){
                return true;
            }
        }

        //just let the if chain be, because why not :(

        //check if head touches left border
        if(lightCycleX[0] < 0){
            return true;
        }
        //checks if head touches right border
        else if(lightCycleX[0] > GamePanel.Constants.SCREEN_WIDTH){
            return true;
        }
        //checks if head touched top border
        else if(lightCycleY[0] < 0){
            return true;
        }
        //checks if head touched bottom border
        else if(lightCycleY[0] > GamePanel.Constants.SCREEN_HEIGHT){
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

        //account for every trail unit other than the head (i > 0)
        for(int i = trailLength; i>0; i--){
            lightCycleX[i] = lightCycleX[i-1];
            lightCycleY[i] = lightCycleY[i-1];
        }

        switch (direction) {
            case Direction.UP:
                //y++
                lightCycleY[0] = lightCycleY[0] - GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.DOWN:
                //y--
                lightCycleY[0] = lightCycleY[0] + GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.LEFT:
                //x--
                lightCycleX[0] = lightCycleX[0] - GamePanel.Constants.UNIT_SIZE;
                break;
            case Direction.RIGHT:
                //x++
                lightCycleX[0] = lightCycleX[0] + GamePanel.Constants.UNIT_SIZE;
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
                if(direction != Direction.RIGHT){
                    direction = Direction.LEFT;
                }
            }
            else if (keyCode == keyMap[3]) {
                if(direction != Direction.LEFT){
                    direction = Direction.RIGHT;
                }
            }
            else if (keyCode == keyMap[0]) {
                if(direction != Direction.DOWN){
                    direction = Direction.UP;
                }
            }
            else if (keyCode == keyMap[1]) {
                if(direction != Direction.UP){
                    direction = Direction.DOWN;
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




