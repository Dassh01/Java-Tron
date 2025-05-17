import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        static final int DELAY = 200;

        //Colors
        static final Color blueHeadColor = new Color(14, 40, 234);
        static final Color blueBodyColor = new Color(3,254,254);
    }

    public final int[] X = new int[Constants.GAME_UNITS];
    public final int[] Y = new int[Constants.GAME_UNITS];

    boolean running = false;

    Timer timer;
    Random random;

    LightCycle blueLightCycle = new LightCycle(X, Y,
            Constants.blueHeadColor, Constants.blueBodyColor);

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);

        //Start key listeners for blue light cycle
        this.addKeyListener(blueLightCycle.getBikeKeyDirectionMonitor());
        this.addKeyListener(blueLightCycle.getBikeKeyThrottleMonitor());

        startGame();
    }

    public void startGame() {
        running = true;
        timer = new Timer(Constants.DELAY,this);
        timer.start();
    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

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
            blueLightCycle.draw(g);

        }
        else {
            gameOver(g);
        }

    }

    public void checkCollisions(){
        //checks if head collide the body
        running = !(blueLightCycle.runLightcycleCollisionCheck());

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