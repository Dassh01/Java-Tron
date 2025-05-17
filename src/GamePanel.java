import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: Make the grid bigger
//TODO: Make the lightcycle slower by default
//TODO: Add speedup / slowdown

public class GamePanel extends JPanel implements ActionListener {

    public static class Constants {
        static final int SCREEN_WIDTH = 800;
        static final int SCREEN_HEIGHT = 800;
        static final int UNIT_SIZE = 15;
        static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
        static final int DELAY = 100; //overall speed of game

        //Colors
        static final Color blueHeadColor = new Color(14, 40, 234);
        static final Color blueBodyColor = new Color(3,254,254);

        static final Color orangeHeadColor = new Color(200,150,30);
        static final Color orangeBodyColor = new Color(242, 192, 53);
    }

    public final int[] X = new int[Constants.GAME_UNITS];
    public final int[] Y = new int[Constants.GAME_UNITS];

    boolean running = false;

    String victor; //TODO: Display victor's name as winner: victor at game end

    Timer timer;
    Random random;

    ArrayList<LightCycle> lightCycles = new ArrayList<>();

    LightCycle blueLightCycle = new LightCycle("Blue",X, Y,
            Constants.blueHeadColor, Constants.blueBodyColor,
            new LightCycle.Pose2d(0,0, LightCycle.Direction.RIGHT));

    LightCycle orangeLightCycle = new LightCycle("Orange",X, Y,
            Constants.orangeHeadColor, Constants.orangeBodyColor,
            new LightCycle.Pose2d(200,0, LightCycle.Direction.RIGHT));
    /**
     * Declare key graphics stuffs and add keyListeners
     */
    GamePanel(){

        lightCycles.add(blueLightCycle);

        random = new Random();
        this.setPreferredSize(new Dimension(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);

        //Start key listeners for lightcycles
        for (LightCycle lightCycle : lightCycles) {
            this.addKeyListener(lightCycle.getBikeKeyDirectionMonitor());
            this.addKeyListener(lightCycle.getBikeKeyThrottleMonitor());
        }

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

    /**
     * @param g the Graphics object to draw onto
     */
    public void draw(Graphics g){
        if(running){
            //draw the grid line on the screen
            int lineSpacing = Constants.SCREEN_HEIGHT/ Constants.UNIT_SIZE;
            for(int i = 0;i< lineSpacing;i++){
                g.drawLine(i* Constants.UNIT_SIZE,0,i* Constants.UNIT_SIZE, Constants.SCREEN_HEIGHT);
                g.drawLine(0,i* Constants.UNIT_SIZE, Constants.SCREEN_WIDTH,i* Constants.UNIT_SIZE);
            }

            //draw the head and the body of the lightbike
            //handles coloring too
            for (LightCycle lightCycle : lightCycles) {
                lightCycle.draw(g);
            }

        }
        else {
            gameOver(g);
        }

    }

    public void checkCollisions(){
        //checks if head collide the body
        for (int i = 0; i < lightCycles.size(); ++i) {
            if (lightCycles.get(i).runLightcycleCollisionCheck()) {
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
        g.setColor(Color.white);
        g.setFont(new Font("Rockwell Extra Bold",Font.BOLD,75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("DEREZZED",(Constants.SCREEN_WIDTH - metrics.stringWidth("DEREZZED"))/2, Constants.SCREEN_HEIGHT/2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            blueLightCycle.updateMovement();
            checkCollisions();
        }
        repaint();
    }
}