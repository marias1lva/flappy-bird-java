import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener { //allow to define a new class with all the functionalities of jpanel
    int boardWidth = 360;
    int boardHeight = 640;

    //variables for images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //game state
    enum GameState { START, PLAYING, GAME_OVER }
    GameState gameState = GameState.START;

    //bird variables
    //if i want to draw the bird on the screen, i need t give an x and y position, as well as a width and height
    int birdX = boardWidth/8; //for the x position (which is basically left and right) i going to place the exposition 1/8 from the left side of the scree
    int birdY = boardHeight/2; //the y position is going to be half of the board height, so from the top of the screen i going to move the bird down halfway
    int birdWidth = 34; //the width of the bird is 34 pixels
    int birdHeight = 24; //the height of the bird is 24 pixels

    //class to hold these values
    //the reason why i created a class was because i can make it easier to access the values just by using these simplify names
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //game logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed (simulate bird moving right)
    int velocityY = 0; //move bird up and down speed
    int gravity = 1; //this means every frame the bird is going to slow down by one pixel

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    //class for the pipes
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    ArrayList<Pipe> pipes; //have many pipes in the game so i need to store them in a list
    Random random = new Random();

    //timers
    Timer gameLoop;
    Timer placePipesTimer;

    //score
    double score = 0; //the score start off with 0 and every time the bird pass a set of pipes, increment the score by one

    //constructor
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true); //make sure that the FlappyBird class which is a jpanel its going to make sure that this is the one that takes in the key events
        addKeyListener(this); //make sure that check three functions over here

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);

        //pipes
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500, e -> placePipes());

        //game timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY = (int)(-pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    //draw images
    public void paintComponent(Graphics g){ //a function of the jpanel
        super.paintComponent(g); //this is just going to invoke the function from jpanel
        draw(g);
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        if(gameState == GameState.GAME_OVER){
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, boardWidth, boardHeight);
        }

        g.setColor(Color.white);
        if(gameState == GameState.START){
            g.setFont(new Font("Arial", Font.BOLD, 36));
            drawCenteredText(g, "Flappy Bird", boardHeight / 3);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            drawCenteredText(g, "Press SPACE to Start", boardHeight / 2);
        }else if(gameState == GameState.PLAYING){
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString(String.valueOf((int) score), 20, 40);
        }else if (gameState == GameState.GAME_OVER){
            g.setFont(new Font("Arial", Font.BOLD, 36));
            drawCenteredText(g, "Game Over", boardHeight / 3);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            drawCenteredText(g, "Score: " + (int) score, boardHeight / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            drawCenteredText(g, "Press SPACE to Restart", boardHeight / 2 + 40);
        }
    }

    public void drawCenteredText(Graphics g, String text, int y){
        FontMetrics fm = g.getFontMetrics();
        int x = (boardWidth - fm.stringWidth(text))/2;
        g.drawString(text, x, y);
    }

    public void move(){
        //in this function i am going to update all the x and y positions of the objects
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for(int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(pipe.x + pipe.width < 0){
                pipes.remove(pipe);
                i--;
                continue;
            }

            if(!pipe.passed && bird.x > pipe.x + pipe.width){ //if the bird has not passed this pipe and the x position of the bird is pass the right side of this pipe
                pipe.passed = true; //mark this pipe as passed
                score += 0.5; //0.5 because there are 2 pipes, so 0.5 * 2 = 1, 1 for each set of pipes
            }

            if(collision(bird, pipe)){
                gameState = GameState.GAME_OVER;
                placePipesTimer.stop();
                gameLoop.stop();
            }
        }

        if(bird.y > boardHeight){
            gameState = GameState.GAME_OVER;
            placePipesTimer.stop();
        }
    }

    public boolean collision(Bird a, Pipe b){
        //formula for detecting collision
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void restartGame(){
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameState = GameState.PLAYING;
        placePipesTimer.start();
        gameLoop.start();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(gameState == GameState.PLAYING){
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if (gameState == GameState.START) {
                gameState = GameState.PLAYING;
                velocityY = -9;
                placePipesTimer.start();
            }else if(gameState == GameState.PLAYING){
                velocityY = -9;
            }else if(gameState == GameState.GAME_OVER){
                restartGame();
            }
        }
    }

    //unused functions
    @Override
    public void keyTyped(KeyEvent e){}

    @Override
    public void keyReleased(KeyEvent e){}
}
