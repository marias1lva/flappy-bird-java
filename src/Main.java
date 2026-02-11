//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //dimensions of window (in pixels)
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //basically when the user clicks on the x button on the window it will terminate the program

        FlappyBird flappyBird = new FlappyBird(); //create an instance of FlappyBird
        frame.add(flappyBird); //add the flappy bird panel to the frame
        frame.pack(); //if i didn´t add this, the width and height would take into account the dimensions of the title bar
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}