package game;

//importing all of the necessary library/functions
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class View {

    // creating the input streams for image icons for the view class
    private InputStream shipStream = this.getClass().getResourceAsStream("/resources/images/ship.png");
    private InputStream oceanStream = this.getClass().getResourceAsStream("/resources/images/ocean.png");
    private InputStream hitShipStream = this.getClass().getResourceAsStream("/resources/images/hitShip.png");
    private InputStream hitOceanStream = this.getClass().getResourceAsStream("/resources/images/hitOcean.png");
    private InputStream battleshipsStream = this.getClass().getResourceAsStream("/resources/images/frameIcon.png");

    //creating the images icons which will later use the inputStreams 
    private ImageIcon shipIcon;
    private ImageIcon oceanIcon;
    private ImageIcon hitShipIcon;
    private ImageIcon hitOceanIcon;
    private ImageIcon battleshipsIcon;

    //creating the frame where the client will play the game
    private JFrame frame = new JFrame();

    //creating the titlePanel and and TabbedPane which will have two tabes myBoard/EnemyBoard
    private JPanel titlePanel = new JPanel();
    private JTabbedPane buttons = new JTabbedPane();

    //creating the panels that will be added to the TabbedPane
    private JPanel myTabPanel = new JPanel();
    private JPanel enemyTabPanel = new JPanel();

    //creating the rest of the panel needed to organize all the components properly
    private JPanel myButtonPanel = new JPanel();
    private JPanel enemyButtonPanel = new JPanel();
    private JPanel tabSideButtons = new JPanel();
    private JPanel statusPanel = new JPanel();
    private JPanel endGamePanel = new JPanel();
    private JPanel endGameButtonsPanel = new JPanel();

    //creating a couple of different labels 
    private JLabel endGameLabel = new JLabel();
    private JLabel playerIdLabel = new JLabel();
    private JLabel scoreLabel = new JLabel();
    private JLabel titleText = new JLabel();

    //creating all of the elements used for the music menu
    private JTextArea messagePlayerLabel = new JTextArea();
    private JMenuBar musicMenuBar = new JMenuBar();

    private JMenu musicMenu = new JMenu("music menu");
    private JMenu songs = new JMenu("songs");
    private JMenuItem songZero = new JMenuItem("Beautiful Dream");
    private JMenuItem songOne = new JMenuItem("Driving Ambition");
    private JMenuItem songTwo = new JMenuItem("Just Chill");
    private JCheckBoxMenuItem pauseButton = new JCheckBoxMenuItem("pause");
    private JSlider volumeSlider = new JSlider(0, 200);

    //creating Yes and No buttons that will ask the user if he wants to play again
    private JButton yesButton = new JButton();
    private JButton noButton = new JButton();

    //creating two arrays of JButtons 
    //on for the user's board and one for the enemy's board
    private JButton[][] myButtons;
    private JButton[][] enemyButtons;

    //creating buttons that will be used in and to end the setup phase
    private JButton finishSetup = new JButton();
    private JButton randomizeGrid = new JButton();

    //to arrays which represents the user's board and his enemy's board
    private int[][] myShipsLocation;
    private int[][] enemyShipsLocation;

    
    private int rowsAndCols;//represents the number of row and cols in the board (they are equal)
    private int playerId;//Represents the the playerId which is 1 or 2 based on if the player connected to the server first or second

    /**
     * 
     * @param rowAndCols represents the number of row and cols in the board (they are equal) 
     * @param myShipsLocation The location of the ships before the user set them up 
     * (currently will always be the same but still made it a variable in order to make the code more flexible)
     * @param playerId Represents the the playerId which is 1 or 2 based on if the player connected to the server first or second
     */
    public View(int rowAndCols, int[][] myShipsLocation, int playerId) {

        //creating all the icons using the input streams
        try {
            shipIcon = new ImageIcon(ImageIO.read(shipStream));
            oceanIcon = new ImageIcon(ImageIO.read(oceanStream));
            hitShipIcon = new ImageIcon(ImageIO.read(hitShipStream));
            hitOceanIcon = new ImageIcon(ImageIO.read(hitOceanStream));
            battleshipsIcon = new ImageIcon(ImageIO.read(battleshipsStream));

        } catch (IOException e) {
            sendPlayerMessage(new Font("Arial", Font.BOLD, 13), "A problem has occured while loading the images");
            e.printStackTrace();
            unexpectedErrorHasOccurred("A problem occurred while trying to load the images", "Error Loading Images");
        }

        this.playerId = playerId;//setting the parameters as of the function to their respective class variables
        this.myShipsLocation = myShipsLocation;
        this.rowsAndCols = rowAndCols;

        myButtons = new JButton[rowAndCols][rowAndCols];//initializing both myButtons and enemyButtons
        enemyButtons = new JButton[rowAndCols][rowAndCols];

        // setting the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(1000, 1000);
        frame.setResizable(true);
        frame.setIconImage(battleshipsIcon.getImage());
        frame.setVisible(true);

        // setting the title text
        titleText.setBackground(new Color(25, 25, 25));
        titleText.setForeground(new Color(230, 69, 0));
        titleText.setText("BATTLESHIPS - THE GAME");
        titleText.setFont(new Font("Century Gothic" ,Font.BOLD, 60));
        titleText.setHorizontalAlignment(JLabel.CENTER);
        titleText.setBackground(new Color(50, 50, 50));
        titleText.setOpaque(true);

        // statusBar panel setup
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.PAGE_AXIS));

        // setting the labels for the status bar

        //setting the playerIdLabel which represents info about the player 
        //in particular if hes player 1 or 2
        playerIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerIdLabel.setText("        Player #" + playerId);
        playerIdLabel.setHorizontalAlignment(JLabel.CENTER);
        playerIdLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));

        //setting the score label which shows the score
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        scoreLabel.setText("            You : 0, Enemy : 0");
        scoreLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        //setting the messagePlayerLabel which is used to send messages to the player
        messagePlayerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        //setting up the endGamePanel which is the panel that pops up when one player wins
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.Y_AXIS)); 
        endGameLabel.setText("Do you want a rematch?");
        endGameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        endGameButtonsPanel.setLayout(new BoxLayout(endGameButtonsPanel, BoxLayout.X_AXIS));

        //setting the yes and no buttons which will be added to the endGamePanel
        yesButton.setText("Yes");
        noButton.setText("No");

        //adding the yes and no buttons to the endGamePanel
        endGameButtonsPanel.add(yesButton);
        endGameButtonsPanel.add(noButton);

        //adding both label I created earlier to the endGamePanel
        endGamePanel.add(endGameLabel);
        endGamePanel.add(endGameButtonsPanel);

        // setting the layout for a bunch of panels
        titlePanel.setLayout(new BorderLayout());
        myTabPanel.setLayout(new BorderLayout());
        enemyTabPanel.setLayout(new BorderLayout());
        myButtonPanel.setLayout(new GridLayout(rowAndCols, rowAndCols));
        enemyButtonPanel.setLayout(new GridLayout(rowAndCols, rowAndCols));
        tabSideButtons.setLayout(new BoxLayout(tabSideButtons, BoxLayout.PAGE_AXIS));

        //setting up the song menu in the sideButtonPanel
        volumeSlider.setToolTipText("Volume Slider");

        //adding the menuItems for the music menu into the song menu 
        songs.add(songZero);
        songs.add(songOne);
        songs.add(songTwo);

        //adding the songMenu and two additional buttons to the music menu
        musicMenu.add(songs);
        musicMenu.add(pauseButton);
        musicMenu.add(volumeSlider);

        musicMenuBar.add(musicMenu);

        //make the pauseButton and volumeSlider initially disabled because at the beginning there is no music running
        pauseButton.setEnabled(false);
        volumeSlider.setEnabled(false);

        //adding the buttons to both the myButtons 2d array and enemyButtons 2d array
        //and setting all the buttons icons accordingly
        for (int i = 0; i < rowAndCols; i++) {
            for (int j = 0; j < rowAndCols; j++) {
                myButtons[i][j] = new JButton();
                enemyButtons[i][j] = new JButton();
                myButtonPanel.add(myButtons[i][j]);
                enemyButtonPanel.add(enemyButtons[i][j]);
                enemyButtons[i][j].setIcon(oceanIcon);

                if (myShipsLocation[j][i] == 1) {
                    myButtons[i][j].setIcon(shipIcon);
                } else {
                    myButtons[i][j].setIcon(oceanIcon);
                }
            }
        }

        // setting the extra buttons for the myTabSideButtons

        //setting up finishSetup 
        finishSetup.setText("Finish Setup");
        finishSetup.setHorizontalAlignment(SwingConstants.CENTER);
        finishSetup.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
        finishSetup.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        finishSetup.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        finishSetup.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        //setting up randomizeGrid 
        randomizeGrid.setText("Randomize Grid");
        randomizeGrid.setHorizontalAlignment(SwingConstants.CENTER);
        randomizeGrid.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
        randomizeGrid.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        randomizeGrid.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        randomizeGrid.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        //setting up messagePlayerLabel 
        messagePlayerLabel.setMinimumSize(new Dimension(0, 0));
        messagePlayerLabel.setPreferredSize(new Dimension(0,0));
        messagePlayerLabel.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 15));
        messagePlayerLabel.setEditable(false);
        messagePlayerLabel.setHighlighter(null);

        //setting up the all the stuff for the music menu

        //setting up musicMenuBar
        musicMenuBar.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        musicMenuBar.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        musicMenuBar.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        //setting up musicMenu
        musicMenu.setHorizontalAlignment(SwingConstants.CENTER);
        musicMenu.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 20));
        musicMenu.setMinimumSize(new Dimension(0, (int) frame.getSize().getHeight() / 10));
        musicMenu.setPreferredSize(new Dimension((int) (frame.getSize().getWidth() / 5), (int) frame.getSize().getHeight() / 13));
        musicMenu.setMaximumSize(new Dimension(1000, (int) frame.getSize().getHeight() / 10));

        // setting up status panel
        statusPanel.add(playerIdLabel);
        statusPanel.add(scoreLabel);
        statusPanel.add(messagePlayerLabel);
        statusPanel.add(endGamePanel); 
        endGamePanel.setVisible(false);
        messagePlayerLabel.setVisible(false);

        // adding components to myTabSideButtons
        tabSideButtons.add(finishSetup);
        tabSideButtons.add(randomizeGrid);
        tabSideButtons.add(musicMenuBar);
        tabSideButtons.add(statusPanel);

        //adding components to enemy
        enemyTabPanel.add(enemyButtonPanel, BorderLayout.CENTER);

        // adding components to myTabPanel
        myTabPanel.add(myButtonPanel, BorderLayout.CENTER);
        myTabPanel.add(tabSideButtons, BorderLayout.EAST);

        //adding both panels to the TabbedPane
        buttons.addTab("your Board", myTabPanel);
        buttons.addTab("enemy Board", enemyTabPanel);

        //adding the titleLabel to the titlePanel        
        titlePanel.add(titleText);

        //adding the Title and the TabbedPane to the frame
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttons, BorderLayout.CENTER);
    }

    /**
     * this function takes a 
     * @param buttons a 2d array of JButtons 
     * @param ActionListener a class which implements ActionListener
     * and adds to all the JButtons in the array the ActionListener
     */
    public static void addActionListenerButtons(JButton[][] buttons, Object ActionListener) {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[0].length; j++) {
                buttons[i][j].addActionListener((java.awt.event.ActionListener) ActionListener);
            }
        }
    }

    /**
     * this function takes a 
     * @param menuItems an array of JMenuItems
     * @param ActionListener a class which implements ActionListener
     *  and adds to all the JMenuItems the ActionListener 
     */    
    public static void addActionListenerSongMenuItems (JMenuItem [] menuItems, Object ActionListener) {
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].addActionListener((java.awt.event.ActionListener) ActionListener);
        }
    }

    /**
     * 
     * @param myShipsLocation a 2d array which represents the state of the player's board
     * 1 - ship that wasn't hit
     * 0 - ocean that wasn't hit
     * -1 - hit ship
     *  anything else - hit ocean 
     *  this function then updates the icon of each button in my buttons image based myShipsLocation 
     */
    public void refreshMyBoard(int[][] myShipsLocation) {
        this.myShipsLocation = myShipsLocation;

        for (int i = 0; i < rowsAndCols; i++) {
            for (int j = 0; j < rowsAndCols; j++) {

                myButtons[j][i].setIcon(null);

                if (myShipsLocation[i][j] == 1) {
                    myButtons[j][i].setIcon(shipIcon);

                } else if (myShipsLocation[i][j] == 0){
                    myButtons[j][i].setIcon(oceanIcon);

                }else if(myShipsLocation[i][j] == -1){
                    myButtons[j][i].setIcon(hitShipIcon);

                } else {
                    myButtons[j][i].setIcon(hitOceanIcon);
                }
            }
        }
    }

    /**
     * 
     * @param enemyShipsLocation a 2d array which represents the state of the enemy's board
     * -1 - hit ship
     * -2 - hit ocean
     *  anything else - ocean 
     *  (because the user can't see a enemy ship which wasn't hit) 
     */
    public void refreshEnemyBoard(int[][] enemyShipsLocation) {
        this.enemyShipsLocation = enemyShipsLocation;

        for (int i = 0; i < rowsAndCols; i++) {
            for (int j = 0; j < rowsAndCols; j++) {

                enemyButtons[i][j].setIcon(oceanIcon);

                if (enemyShipsLocation[i][j] == -1) {
                    enemyButtons[i][j].setIcon(hitShipIcon);

                } else if (enemyShipsLocation[i][j] == -2) {
                    enemyButtons[i][j].setIcon(hitOceanIcon);
                }
            }
        }
    }

    /**
     * resets messagePlayerLabel
     * and makes it invisible
     */
    public void resetMessagePlayer() {
        messagePlayerLabel.setText("");
        messagePlayerLabel.setVisible(false);
    }

    /**
     * 
     * @param font a certain font
     * @param text a string with text
     *  this function sets messagePlayerLabels font to the parameter and its text to text
     * additionally it makes messagePlayerLabel visible
     */
    public void sendPlayerMessage(Font font, String text) {
        messagePlayerLabel.setText(text);
        messagePlayerLabel.setFont(font);
        messagePlayerLabel.setVisible(true);
    }

    /**
     * opens a pane which thanks the user for playing 
     * then exits the code
     */
    public void closeGui() {
        JOptionPane.showMessageDialog(null,
        "Goodbye and thanks for playing!", 
        "Goodbye",
        JOptionPane.PLAIN_MESSAGE);

        System.exit(0);
    }

    /**
     * creates an error pane 
     * then closes the program  
     * @param text the text that appears on the pane 
     * @param title the title of the pane 
     */
    public static void unexpectedErrorHasOccurred(String text, String title) {
        JOptionPane.showMessageDialog(null, text, title , JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    /**
     * 
     * @param myWins an integer which represents the amount of times the player won
     * @param enemyWins an integer which represents the amount of times the enemy won
     * opens a pane which congratulates the player on winning and shows how many times each player won 
     */
    public void youWonPane(int myWins, int enemyWins){
        JOptionPane.showMessageDialog(null,
        "Good Job You Won \n The Score is now: \n" + "You : " + myWins + " ,Enemy : " + enemyWins, 
        "Victory",
        JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * 
     * @param myWins an integer which represents the amount of times the player won
     * @param enemyWins an integer which represents the amount of times the enemy won
     * opens a pane which tells the player he lost and comforts him. Additionally, shows how many times each player won 
     */
    public void youLostPane(int myWins, int enemyWins){
        JOptionPane.showMessageDialog(null,
        "Nice try... Maybe next time \n The Score is now: \n" + "You : " + myWins + " ,Enemy : " + enemyWins, 
        "Defeat",
        JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * informs the user that the enemy did not agree to have a rematch and closes the program
     */
    public void opponentDeclinedRematch() {
        JOptionPane.showMessageDialog(null, "Unfortunately your opponent has declined a rematch so... \n Bye Bye... And stay golden", "Rematch Declined", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }

    /**
     * creates a pane which tells the user that there has an error with the connection to the server 
     * it then asks if user would like to close the program 
     * it then behaves accordingly 
     */
    public static void connectionErrorPane() {
        int dialogButton = JOptionPane.showConfirmDialog (null, "Connection Error: There has been an error with the connection to the server \n Do you want to close the game?","Connection Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        if (dialogButton == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * 
     * @return an array with the three song menuItems
     */
    public JMenuItem[] getSongMenuItems() {
        JMenuItem[] menuItemArray = { songZero, songOne, songTwo };
        return menuItemArray;
    }

    public ImageIcon getShipIcon() {
        return this.shipIcon;
    }

    public ImageIcon getOceanIcon() {
        return this.oceanIcon;
    }

    public ImageIcon getHitShipIcon() {
        return this.hitShipIcon;
    }

    public ImageIcon getHitOceanIcon() {
        return this.hitOceanIcon;
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public JPanel getStatusPanel() {
        return this.statusPanel;
    }

    public JButton getFinishSetup() {
        return this.finishSetup;
    }

    public JButton getRandomizeGrid() {
        return this.randomizeGrid;
    }

    public JPanel getMyTabPanel() {
        return this.myTabPanel;
    }

    public JPanel getMyTabSideButtons() {
        return this.tabSideButtons;
    }

    public JTextArea getMessagePlayerLabel() {
        return this.messagePlayerLabel;
    }

    public JLabel getScoreLabel() {
        return this.scoreLabel;
    }

    public JPanel getEndGamePanel() {
        return this.endGamePanel;
    }

    public JButton getYesButton() {
        return this.yesButton;
    }

    public JButton getNoButton() {
        return this.noButton;
    }

    public JButton[][] getMyButtons() {
        return this.myButtons;
    }

    public JButton[][] getEnemyButtons() {
        return this.enemyButtons;
    }

    public JButton getMyButton(int x, int y) {
        return this.myButtons[x][y];
    }

    public JButton getEnemyButton(int x, int y) {
        return this.enemyButtons[x][y];
    }

    public JSlider getVolumeSlider() {
        return this.volumeSlider;
    }

    public JCheckBoxMenuItem getPauseButton() {
        return this.pauseButton;
    }

    public JMenuBar getMusicMenuBar() {
        return this.musicMenuBar;
    }

    public JMenu getMusicMenu() {
        return this.musicMenu;
    }

    public JMenu getSongs() {
        return this.songs;
    }

    public JMenuItem getSongOne() {
        return this.songZero;
    }

    public JMenuItem getSongTwo() {
        return this.songOne;
    }

    public JMenuItem getSongThree() {
        return this.songTwo;
    }
}
