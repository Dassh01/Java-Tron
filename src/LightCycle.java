import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LightCycle {

    private final int[] X;
    private final int[] Y;

    boolean throttleOn = false;
    boolean slowDownOn = false;

    int tilesPoisonedBehindLimit = 150;
    int tickCount = 0;

    Color headColor;
    Color bodyColor;

    Direction direction = Direction.RIGHT;

    public LightCycle(int[] X, int[] Y, Color headColor, Color bodyColor) {
        this.X = X;
        this.Y = Y;
        this.headColor = headColor;
        this.bodyColor = bodyColor;
    }

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

    public void move(){
        for(int i = tilesPoisonedBehindLimit; i>0; i--){
            X[i] = X[i-1];
            Y[i] = Y[i-1];
        }

        switch (direction){
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

    public class BikeKeyDirectionMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != Direction.RIGHT){
                        direction = Direction.LEFT;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != Direction.LEFT){
                        direction = Direction.RIGHT;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != Direction.DOWN){
                        direction = Direction.UP;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != Direction.UP){
                        direction = Direction.DOWN;
                    }
                    break;
            }
        }
    }

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

    public KeyAdapter getBikeKeyDirectionMonitor() {return new BikeKeyDirectionMonitor();}
    public KeyAdapter getBikeKeyThrottleMonitor() {return new BikeKeyThrottleMonitor();}

    public enum Direction{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}




