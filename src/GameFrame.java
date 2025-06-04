import javax.swing.*;

//this is the "config"
public class GameFrame {
    GameFrame(){
        JFrame gameFrame = new JFrame();
        gameFrame.add(new GamePanel(gameFrame));
        gameFrame.setTitle("Java Tron");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(false);
        gameFrame.pack(); // take our J frame and fit it around all of the component that we add
        gameFrame.setVisible(true);
        gameFrame.setLocationRelativeTo(null); // appear in the middle of the computer screen
    }
}