import javax.swing.*;

//this is the "config"
public class GameFrame extends JFrame {
    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Java Tron");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); // take our J frame and fit it around all of the component that we add
        this.setVisible(true);
        this.setLocationRelativeTo(null); // appear in the middle of the computer screen
    }
}