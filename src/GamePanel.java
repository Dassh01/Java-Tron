import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    //TODO: Introduce 2 player system
    //TODO: Make coordinate system.. work, and make it so that players generate or "spawn" in different areas defined by pose2d
    public static class Constants {
        static final int trailLength = 30;
        static final int SCREEN_WIDTH = 800;
        static final int SCREEN_HEIGHT = 800;
        static final int UNIT_SIZE = 15;
        static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
        static final int DELAY = 100; //overall speed of game //TODO: Fine tune value

        static final String blueName = "blue";
        static final String orangeName = "orange";

        static final boolean debug = true;
        static final int DEBUG_TICKSPEED = 25;
        //Colors
        static final Color blueHeadColor = new Color(14, 40, 234);
        static final Color blueBodyColor = new Color(3,254,254);

        static final Color orangeHeadColor = new Color(230,100,30);
        static final Color orangeBodyColor = new Color(242, 192, 53);

        static LightCycle.Pose2d blueLightCycleInitialPose = new LightCycle.Pose2d(0,0, LightCycle.Direction.DOWN);
        static LightCycle.Pose2d orangeLightCycleInitialPose = new LightCycle.Pose2d(600,600, LightCycle.Direction.UP);
        //Keymaps
        /*
        0 = Up
        1 = Down
        2 = Left
        3 = Right
        4 = Throttle
        5 = Slowdown
         */

        public static final int[] blueKeyMap = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, //Arrow Keys for direction
        KeyEvent.VK_N, KeyEvent.VK_M}; //Throttle & Slowdown

        public static final int[] orangeKeyMap = {KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, //WASD for direction
        KeyEvent.VK_C, KeyEvent.VK_V}; //Throttle & Slowdown
    }

    public final int[] GameX = new int[Constants.GAME_UNITS];
    public final int[] GameY = new int[Constants.GAME_UNITS];

    boolean running = false;
    int globalTicks = 0;

    String victor;

    Timer timer;
    Random random;

    ArrayList<LightCycle> lightCycles = new ArrayList<>();

    LightCycle blueLightCycle = new LightCycle(Constants.blueName,
            Constants.blueHeadColor, Constants.blueBodyColor, Constants.blueLightCycleInitialPose,
            Constants.orangeKeyMap);

    LightCycle orangeLightCycle = new LightCycle(Constants.orangeName,
            Constants.orangeHeadColor, Constants.orangeBodyColor, Constants.orangeLightCycleInitialPose,
            Constants.blueKeyMap);

    JFrame gameFrame;

    /**
     * Declare key graphics stuffs and add keyListeners
     */
    GamePanel(JFrame gameFrame){
        this.gameFrame = gameFrame;

        lightCycles.add(orangeLightCycle);
        lightCycles.add(blueLightCycle);

        random = new Random();
        this.setPreferredSize(new Dimension(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);

        //Start key listeners for lightcycles
        this.addKeyListener(blueLightCycle.getBikeKeyDirectionMonitor());
        this.addKeyListener(blueLightCycle.getBikeKeyThrottleMonitor());
        //BE EXPLICIT
        this.addKeyListener(orangeLightCycle.getBikeKeyThrottleMonitor());
        this.addKeyListener(orangeLightCycle.getBikeKeyDirectionMonitor());

        this.addKeyListener(new ExitKeyListener());

        startGame();
    }

    /**
     * Mostly responsible for starting the timer, controlling game speed
     */
    public void startGame() {
        running = true;
        timer = new Timer(Constants.DELAY,this);
        timer.start();
    }

    /**
     * @param g the <code>Graphics</code> object to paint onto
     */
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void writeLightCycleToDisplay(Graphics g, LightCycle lightCycle) {

        if (lightCycle.tickCount % Constants.DEBUG_TICKSPEED == 0) {
            System.out.println(lightCycle.name + " light cycle head: " +
                    "(" + lightCycle.lightCycleX[0] + "," + lightCycle.lightCycleY[0] + ")");
            System.out.println("Light cycle direction: " + lightCycle.direction);
        }

        for(int i = 0; i < lightCycle.trailLength; i++){
            if(i==0){
                g.setColor(lightCycle.headColor);             //for the head
            }else {
                g.setColor(lightCycle.bodyColor); //for the body

            }

            g.fillRect(lightCycle.lightCycleX[i], lightCycle.lightCycleY[i], GamePanel.Constants.UNIT_SIZE, GamePanel.Constants.UNIT_SIZE);
        }
    }

    /**
     * @param g the Graphics object to draw onto
     */
    public void draw(Graphics g){
        if(running){
            //draw the grid line on the screen
            int lineSpacing = Constants.SCREEN_HEIGHT/ Constants.UNIT_SIZE;

            for(int i = 0; i < lineSpacing; ++i){
                g.drawLine(i* Constants.UNIT_SIZE,0,i * Constants.UNIT_SIZE, Constants.SCREEN_HEIGHT);
                g.drawLine(0,i* Constants.UNIT_SIZE, Constants.SCREEN_WIDTH,i* Constants.UNIT_SIZE);
            }

            //draw the head and the body of the lightbikes
            //handles coloring too
            for (LightCycle lightCycle : lightCycles) {
                writeLightCycleToDisplay(g, lightCycle);
            }

        }
        else {
            gameOver(g);
        }

    }

    private boolean globalCollisionCheck(LightCycle lightCycle) {
        if (lightCycle.name.equals(Constants.blueName)) {
            //blue check
            blueLightCycle.updatePose();
            for (int i = 0; i < Constants.trailLength; ++i) {

                int x = orangeLightCycle.lightCycleX[i];
                int y = orangeLightCycle.lightCycleY[i];

                if ((x == blueLightCycle.lightCyclePose.x) && (y == blueLightCycle.lightCyclePose.y)) {
                    System.out.println("Global collision triggered");
                    return true;
                }
            }
        } else {
            //orange check
            orangeLightCycle.updatePose();
            for (int i = 0; i < Constants.trailLength; ++i) {

                int x = blueLightCycle.lightCycleX[i];
                int y = blueLightCycle.lightCycleY[i];

                if ((x == orangeLightCycle.lightCyclePose.x) && (y == orangeLightCycle.lightCyclePose.y)) {
                    System.out.println("Global collision triggered");
                    return true;
                }
            }
        }

        return false;

    }

    public void checkCollisions(){

        for (int i = 0; i < lightCycles.size(); ++i) {
            LightCycle lightcycle = lightCycles.get(i);

            boolean lightCycleLocalCheckOK = lightcycle.runLightcycleLocalCollisionCheck();
            boolean lightCycleGlobalCheckOK = globalCollisionCheck(lightcycle);

            if (globalTicks % Constants.DEBUG_TICKSPEED == 0 && Constants.debug) {
                System.out.println(lightcycle.name + " collison: " + "Global: " + lightCycleGlobalCheckOK + ", Local: " + lightCycleLocalCheckOK);
            }

            if (lightCycleGlobalCheckOK || lightCycleLocalCheckOK) {
                running = false;
                //Static structure for 2 players retrieving the alternate's name
                //Also works for 1 player since It's not directly accessing indexes
                if (i == 1) {
                    victor = lightCycles.getFirst().name;
                } else { victor = lightCycles.getLast().name; }
            }
        }
        if(!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        //Game over text
        Color endTextColor = victor.equals(Constants.blueName) ? Constants.blueHeadColor : Constants.orangeHeadColor;
        g.setColor(endTextColor);
        g.setFont(new Font("Rockwell Extra Bold",Font.BOLD,75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("DEREZZED",(Constants.SCREEN_WIDTH - metrics.stringWidth("DEREZZED"))/2, Constants.SCREEN_HEIGHT/2);
    }

    /**
     * Periodic update function
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if(running){
            ++globalTicks;
            blueLightCycle.updateMovement();
            orangeLightCycle.updateMovement();
            checkCollisions();
        }
        repaint();
    }

    /**
     * Responsible for handling game restarts
     */
    public class ExitKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_BACK_SPACE) {
                new GameFrame();
                gameFrame.dispose();
            }
        }

    }
}